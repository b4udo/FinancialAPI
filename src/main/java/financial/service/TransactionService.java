package financial.service;

import financial.model.Account;
import financial.model.Transaction;
import financial.repository.AccountRepository;
import financial.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;

  public TransactionService(
      TransactionRepository transactionRepository, AccountRepository accountRepository) {
    this.transactionRepository = transactionRepository;
    this.accountRepository = accountRepository;
  }

  public List<Transaction> getAllTransactions() {
    return transactionRepository.findAll();
  }

  public Optional<Transaction> getTransactionById(Long id) {
    return transactionRepository.findById(id);
  }

  public List<Transaction> getTransactionsByAccountId(Long accountId) {
    return transactionRepository.findByAccountId(accountId);
  }

  @Transactional
  public Transaction createTransaction(Transaction transaction) {
    transaction.setId(null);

    Long accId = transaction.getAccount().getId();
    Account account =
        accountRepository
            .findById(accId)
            .orElseThrow(() -> new IllegalArgumentException("Account non trovato: " + accId));

    BigDecimal newBalance = account.getBalance().subtract(transaction.getAmount());
    account.setBalance(newBalance);
    accountRepository.save(account);

    Transaction savedTx = transactionRepository.save(transaction);

    savedTx.setAccount(account);
    return savedTx;
  }

  public void deleteTransaction(Long id) {
    transactionRepository.deleteById(id);
  }
}
