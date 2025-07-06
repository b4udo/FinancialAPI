package financial.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import financial.config.JacksonConfig;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // ← aggiunto
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FinancialController.class)
@AutoConfigureMockMvc(addFilters = false) // ← bypassa i filtri di sicurezza
@Import({SecurityConfig.class, JacksonConfig.class})
@ActiveProfiles("test")
class FinancialControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AccountService accountService;
  @MockBean private TransactionService transactionService;
  @MockBean private JwtService jwtService;
  @MockBean private JwtAuthenticationFilter jwtAuthFilter;
  @MockBean private UserService userService;

  @Test
  @WithMockUser(roles = "USER")
  void healthCheck_returnsOK() throws Exception {
    mockMvc
        .perform(get("/api/health"))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"))
        .andExpect(content().contentType("text/plain;charset=UTF-8")); // opzionale, ma più completo
  }

  @Test
  @WithMockUser(roles = "USER")
  void getAllAccounts_returnsJson() throws Exception {
    List<Account> accounts =
        Arrays.asList(
            Account.builder()
                .id(1L)
                .ownerName("A")
                .balance(BigDecimal.valueOf(100.00))
                .accountType(AccountType.CHECKING)
                .build(),
            Account.builder()
                .id(2L)
                .ownerName("B")
                .balance(BigDecimal.valueOf(200.00))
                .accountType(AccountType.SAVINGS)
                .build());
    when(accountService.getAllAccounts()).thenReturn(accounts);

    mockMvc
        .perform(get("/api/accounts").accept(MediaType.APPLICATION_JSON))
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
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(1));
  }
}
