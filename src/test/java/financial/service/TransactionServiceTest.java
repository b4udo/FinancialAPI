package financial.service;

import financial.model.Account;
import financial.model.Transaction;
import financial.repository.AccountRepository;
import financial.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        transactionService = new TransactionService(transactionRepository, accountRepository);
    }

    @Test
    void testGetAllTransactions() {
        List<Transaction> expected = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.findAll()).thenReturn(expected);
        List<Transaction> actual = transactionService.getAllTransactions();
        assertEquals(2, actual.size());
    }

    @Test
    void testGetTransactionById() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        Optional<Transaction> found = transactionService.getTransactionById(1L);
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void testCreateTransaction() {
        // prepara dati
        Transaction input = new Transaction();
        Account account = new Account();
        account.setId(1L);
        account.setBalance(500.0);
        input.setAccount(account);
        input.setAmount(100.0);

        // stub repository
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Transaction saved = new Transaction();
        saved.setId(1L);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        Transaction result = transactionService.createTransaction(input);
        assertEquals(1L, result.getId());
        verify(accountRepository).save(argThat(a -> a.getBalance() == 400.0));
    }

    @Test
    void testDeleteTransaction() {
        transactionService.deleteTransaction(1L);
        verify(transactionRepository, times(1)).deleteById(1L);
    }
}