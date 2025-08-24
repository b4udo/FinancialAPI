package financial.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import financial.config.JacksonConfig;
import financial.exception.AccountNotFoundException;
import financial.model.Account;
import financial.model.AccountType;
import financial.model.Transaction;
import financial.security.JwtAuthenticationFilter;
import financial.security.JwtService;
import financial.security.SecurityConfig;
import financial.service.AccountService;
import financial.service.TransactionService;
import financial.service.UserService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(FinancialController.class)
@AutoConfigureMockMvc(addFilters = false) // bypassa i filtri di sicurezza per i test
@Import({ SecurityConfig.class, JacksonConfig.class })
@ActiveProfiles("test")
class FinancialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthFilter;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "USER")
    void healthCheck_returnsOK() throws Exception {
        mockMvc
            .perform(get("/api/health"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(content().string("OK"))
            .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllAccounts_returnsJson() throws Exception {
        List<Account> accounts = Arrays.asList(
            Account
                .builder()
                .id(1L)
                .ownerName("A")
                .balance(BigDecimal.valueOf(100.00))
                .accountType(AccountType.CHECKING)
                .build(),
            Account
                .builder()
                .id(2L)
                .ownerName("B")
                .balance(BigDecimal.valueOf(200.00))
                .accountType(AccountType.SAVINGS)
                .build()
        );
        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc
            .perform(get("/api/accounts").accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].ownerName").value("A"))
            .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAccountTransactions_returnsList() throws Exception {
        Transaction tx1 = Transaction.builder().id(1L).amount(new BigDecimal("100.00")).build();
        Transaction tx2 = Transaction.builder().id(2L).amount(new BigDecimal("200.00")).build();
        List<Transaction> txs = Arrays.asList(tx1, tx2);
        when(transactionService.getTransactionsByAccountId(1L)).thenReturn(txs);

        mockMvc
            .perform(get("/api/accounts/1/transactions").accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAccountById_whenExists_returnsAccount() throws Exception {
        Account account = Account
            .builder()
            .id(1L)
            .ownerName("Test Owner")
            .balance(BigDecimal.valueOf(1000.00))
            .accountType(AccountType.CHECKING)
            .build();

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(account));

        mockMvc
            .perform(get("/api/accounts/1").accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.ownerName").value("Test Owner"))
            .andExpect(jsonPath("$.balance").value(1000.00))
            .andExpect(jsonPath("$.accountType").value("CHECKING"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAccountById_whenNotExists_returns404() throws Exception {
        when(accountService.getAccountById(999L)).thenReturn(Optional.empty());

        mockMvc
            .perform(get("/api/accounts/999").accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAccount_withValidData_returnsCreated() throws Exception {
        Account savedAccount = Account
            .builder()
            .id(1L)
            .ownerName("New Owner")
            .balance(BigDecimal.valueOf(500.00))
            .accountType(AccountType.SAVINGS)
            .build();

        when(accountService.createAccount(any(Account.class))).thenReturn(savedAccount);

        mockMvc
            .perform(
                post("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"ownerName\":\"New Owner\",\"balance\":500.00,\"accountType\":\"SAVINGS\"}")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.ownerName").value("New Owner"))
            .andExpect(jsonPath("$.balance").value(500.00))
            .andExpect(jsonPath("$.accountType").value("SAVINGS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAccount_withValidData_returnsUpdatedAccount() throws Exception {
        Account existingAccount = Account
            .builder()
            .id(1L)
            .ownerName("Original Owner")
            .balance(BigDecimal.valueOf(500.00))
            .accountType(AccountType.CHECKING)
            .build();

        Account updatedAccount = Account
            .builder()
            .id(1L)
            .ownerName("Updated Owner")
            .balance(BigDecimal.valueOf(750.00))
            .accountType(AccountType.CHECKING)
            .build();

        // se il service controlla esistenza tramite getAccountById
        when(accountService.getAccountById(1L)).thenReturn(Optional.of(existingAccount));
        // mock corretto: updateAccount(id, account)
        when(accountService.updateAccount(eq(1L), any(Account.class))).thenReturn(updatedAccount);

        mockMvc
            .perform(
                put("/api/accounts/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"ownerName\":\"Updated Owner\",\"balance\":750.00,\"accountType\":\"CHECKING\"}")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.ownerName").value("Updated Owner"))
            .andExpect(jsonPath("$.balance").value(750.00))
            .andExpect(jsonPath("$.accountType").value("CHECKING"));

        // opzionale: verifica che il service sia stato chiamato
        verify(accountService, times(1)).updateAccount(eq(1L), any(Account.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAccount_whenExists_returns204() throws Exception {
        doNothing().when(accountService).deleteAccount(1L);

        mockMvc
            .perform(delete("/api/accounts/1"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent());

        verify(accountService, times(1)).deleteAccount(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAccount_whenNotExists_returns404() throws Exception {
        // usa la custom exception (runtime)
        doThrow(new AccountNotFoundException("Account not found")).when(accountService).deleteAccount(999L);

        mockMvc
            .perform(delete("/api/accounts/999"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());

        verify(accountService, times(1)).deleteAccount(999L);
    }
}
