package financial.service;

import financial.model.Account;
import financial.model.Transaction;
import financial.model.TransactionCategory;
import financial.repository.AccountRepository;
import financial.repository.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Timed(value = "transaction.account", description = "Time taken to get transactions by account")
    @Cacheable(value = "account-transactions", key = "#accountId")
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        log.info("Fetching transactions for account: {}", accountId);
        return transactionRepository.findByAccountId(accountId);
    }

    @Timed(value = "transaction.stats", description = "Time taken to get category statistics")
    @Cacheable(value = "statistics", key = "#startDate + #endDate")
    public Map<TransactionCategory, BigDecimal> getCategoryStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculating category statistics from {} to {}", startDate, endDate);

        List<Transaction> transactions = transactionRepository.findByTimestampBetween(startDate, endDate);
        return transactions
            .stream()
            .collect(
                Collectors.groupingBy(
                    Transaction::getCategory,
                    Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                )
            );
    }

    @Timed(value = "transaction.search", description = "Time taken to search transactions")
    @Cacheable(value = "transactions", key = "#category + #minAmount + #maxAmount + #startDate + #endDate")
    @CircuitBreaker(name = "transactionSearch", fallbackMethod = "searchTransactionsFallback")
    @Retry(name = "transactionSearch")
    public List<Transaction> searchTransactions(
        TransactionCategory category,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        log.info(
            "Searching transactions with category: {}, minAmount: {}, maxAmount: {}, startDate: {}, endDate: {}",
            category,
            minAmount,
            maxAmount,
            startDate,
            endDate
        );

        return transactionRepository.findByFilters(category, minAmount, maxAmount, startDate, endDate);
    }

    public List<Transaction> searchTransactionsFallback(
        TransactionCategory category,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Exception e
    ) {
        log.warn("Fallback for searchTransactions executed due to: {}", e.getMessage());
        return List.of(); // Ritorna una lista vuota come fallback
    }

    public List<Transaction> getUpcomingRecurringTransactions() {
        return transactionRepository.findByIsRecurringTrueAndNextExecutionDateAfter(LocalDateTime.now());
    }

    @Autowired
    private TransactionService self;

    public Map<String, Object> getAnnualSummary(int year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        List<Transaction> yearTransactions = transactionRepository.findByTimestampBetween(startOfYear, endOfYear);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalTransactions", yearTransactions.size());
        summary.put(
            "totalAmount",
            yearTransactions.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        summary.put("categoryBreakdown", self.getCategoryStatistics(startOfYear, endOfYear));

        return summary;
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        transaction.setId(null);

        Long accId = transaction.getAccount().getId();
        Account account = accountRepository
            .findById(accId)
            .orElseThrow(() -> new IllegalArgumentException("Account non trovato: " + accId));

        BigDecimal newBalance = account.getBalance().subtract(transaction.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction savedTx = transactionRepository.save(transaction);

        savedTx.setAccount(account);

        if (transaction.getAmount().compareTo(new BigDecimal("1000")) > 0) {
            transaction.setImportant(true);
            notificationService.notifyImportantTransaction(transaction);
        }

        if (transaction.isRecurring() && transaction.getNextExecutionDate() != null) {
            notificationService.notifyRecurringTransactionScheduled(transaction);
        }

        return savedTx;
    }

    @Timed(value = "transaction.save", description = "Time taken to save a transaction")
    @CacheEvict(value = { "transactions", "statistics" }, allEntries = true)
    public Transaction saveTransaction(Transaction transaction) {
        log.info("Saving transaction: {}", transaction);
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 1 * * ?") // Esegue ogni giorno all'1:00
    @Transactional
    public void processRecurringTransactions() {
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> dueTransactions = transactionRepository.findByIsRecurringTrueAndNextExecutionDateBefore(now);

        for (Transaction transaction : dueTransactions) {
            // Crea una nuova transazione basata su quella ricorrente
            Transaction newTransaction = new Transaction();
            newTransaction.setAmount(transaction.getAmount());
            newTransaction.setDescription(transaction.getDescription());
            newTransaction.setCategory(transaction.getCategory());
            newTransaction.setAccount(transaction.getAccount());
            newTransaction.setTimestamp(now);

            transactionRepository.save(newTransaction);

            // Aggiorna la data della prossima esecuzione
            switch (transaction.getRecurrencePeriod()) {
                case "DAILY" -> transaction.setNextExecutionDate(now.plusDays(1));
                case "WEEKLY" -> transaction.setNextExecutionDate(now.plusWeeks(1));
                case "MONTHLY" -> transaction.setNextExecutionDate(now.plusMonths(1));
                case "YEARLY" -> transaction.setNextExecutionDate(now.plusYears(1));
            }

            transactionRepository.save(transaction);
        }
    }
}
