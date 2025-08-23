package financial.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import financial.model.Account;
import financial.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AccountServiceTest {

    @Mock
    private AccountRepository repo;

    @InjectMocks
    private AccountService service;

    private Account sample;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sample = Account.builder().id(1L).ownerName("Mario Rossi").balance(BigDecimal.valueOf(1000.0)).build();
    }

    @Test
    void getAllAccounts_returnsList() {
        when(repo.findAll()).thenReturn(List.of(sample));
        var result = service.getAllAccounts();
        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void getAccountById_found() {
        when(repo.findById(1L)).thenReturn(Optional.of(sample));
        var found = service.getAccountById(1L);
        assertTrue(found.isPresent());
        assertEquals(sample.getOwnerName(), found.get().getOwnerName());
    }

    @Test
    void createAccount_assignsIdAndSaves() {
        var toCreate = Account.builder().ownerName("Luigi Verdi").balance(BigDecimal.valueOf(500.0)).build();
        when(repo.save(any(Account.class))).thenReturn(sample);
        var saved = service.createAccount(toCreate);
        assertEquals(sample.getId(), saved.getId());
        verify(repo).save(any(Account.class));
    }
}
