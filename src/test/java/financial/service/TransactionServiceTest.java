package financial.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import financial.model.Account;
import financial.model.AccountType;
import financial.model.Transaction;
import financial.model.TransactionCategory;
import financial.repository.AccountRepository;
import financial.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransactionServiceTest {

  @Mock private TransactionRepository transactionRepository;

  @Mock private AccountRepository accountRepository;

  @Mock private NotificationService notificationService;

  @InjectMocks private TransactionService transactionService;

  private Transaction recurringTransaction;
  private Transaction importantTransaction;
  private Account account;

  @BeforeEach
  void setUp() {
    account =
        Account.builder()
            .id(1L)
            .ownerName("Test User")
            .balance(new BigDecimal("1000.00"))
            .accountType(AccountType.CHECKING)
            .build();

    recurringTransaction =
        Transaction.builder()
            .id(1L)
            .amount(new BigDecimal("100.00"))
            .category(TransactionCategory.BILLS)
            .description("Monthly Bill")
            .timestamp(LocalDateTime.now())
            .isRecurring(true)
            .recurrencePeriod("MONTHLY")
            .nextExecutionDate(LocalDateTime.now().plusMonths(1))
            .account(account)
            .build();

    importantTransaction =
        Transaction.builder()
            .id(2L)
            .amount(new BigDecimal("1500.00"))
            .category(TransactionCategory.SALARY)
            .description("Monthly Salary")
            .timestamp(LocalDateTime.now())
            .isRecurring(false)
            .account(account)
            .build();
  }

  @Test
  void createTransaction_WithRecurring_ShouldNotifyAndSave() {
    // Arrange
    when(accountRepository.findById(any())).thenReturn(Optional.of(account));
    when(transactionRepository.save(any())).thenReturn(recurringTransaction);

    // Act
    Transaction result = transactionService.createTransaction(recurringTransaction);

    // Assert
    assertNotNull(result);
    verify(notificationService).notifyRecurringTransactionScheduled(any());
    verify(transactionRepository).save(any());
  }

  @Test
  void createTransaction_WithImportantAmount_ShouldNotifyAndSave() {
    // Arrange
    when(accountRepository.findById(any())).thenReturn(Optional.of(account));
    when(transactionRepository.save(any())).thenReturn(importantTransaction);

    // Act
    Transaction result = transactionService.createTransaction(importantTransaction);

    // Assert
    assertNotNull(result);
    verify(notificationService).notifyImportantTransaction(any());
    verify(transactionRepository).save(any());
  }

  @Test
  void getCategoryStatistics_ShouldReturnCorrectTotals() {
    // Arrange
    LocalDateTime start = LocalDateTime.now().minusMonths(1);
    LocalDateTime end = LocalDateTime.now();

    List<Transaction> transactions =
        Arrays.asList(
            Transaction.builder()
                .amount(new BigDecimal("100.00"))
                .category(TransactionCategory.BILLS)
                .build(),
            Transaction.builder()
                .amount(new BigDecimal("200.00"))
                .category(TransactionCategory.BILLS)
                .build());

    when(transactionRepository.findByTimestampBetween(start, end)).thenReturn(transactions);

    // Act
    Map<TransactionCategory, BigDecimal> result =
        transactionService.getCategoryStatistics(start, end);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(new BigDecimal("300.00"), result.get(TransactionCategory.BILLS));
  }

  @Test
  void getUpcomingRecurringTransactions_ShouldReturnOnlyFutureRecurring() {
    // Arrange
    List<Transaction> expectedTransactions = Arrays.asList(recurringTransaction);
    when(transactionRepository.findByIsRecurringTrueAndNextExecutionDateAfter(any()))
        .thenReturn(expectedTransactions);

    // Act
    List<Transaction> result = transactionService.getUpcomingRecurringTransactions();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).isRecurring());
    assertEquals("MONTHLY", result.get(0).getRecurrencePeriod());
  }

  @Test
  void processRecurringTransactions_ShouldCreateNewTransactionsAndUpdateDates() {
    // Arrange
    when(transactionRepository.findByIsRecurringTrueAndNextExecutionDateBefore(any()))
        .thenReturn(Arrays.asList(recurringTransaction));
    when(transactionRepository.save(any())).thenReturn(recurringTransaction);

    // Act
    transactionService.processRecurringTransactions();

    // Assert
    verify(transactionRepository, times(2))
        .save(any()); // Una volta per la nuova transazione, una per l'aggiornamento
  }

  @Test
  void getAnnualSummary_ShouldReturnCorrectSummary() {
    // Arrange
    List<Transaction> yearTransactions =
        Arrays.asList(
            Transaction.builder()
                .amount(new BigDecimal("100.00"))
                .category(TransactionCategory.BILLS)
                .build(),
            Transaction.builder()
                .amount(new BigDecimal("200.00"))
                .category(TransactionCategory.GROCERIES)
                .build());

    when(transactionRepository.findByTimestampBetween(any(), any())).thenReturn(yearTransactions);

    // Act
    Map<String, Object> result = transactionService.getAnnualSummary(2025);

    // Assert
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(2, result.get("totalTransactions"));
    assertEquals(new BigDecimal("300.00"), result.get("totalAmount"));

    @SuppressWarnings("unchecked")
    Map<TransactionCategory, BigDecimal> categoryBreakdown =
        (Map<TransactionCategory, BigDecimal>) result.get("categoryBreakdown");
    assertEquals(2, categoryBreakdown.size());
  }
}
