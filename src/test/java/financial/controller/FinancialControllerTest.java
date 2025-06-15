package financial.controller;

import financial.model.Account;
import financial.model.Transaction;
import financial.service.AccountService;
import financial.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinancialController.class)
class FinancialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @Test
    void healthCheck_returnsOK() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void getAllAccounts_returnsJson() throws Exception {
        List<Account> accounts = Arrays.asList(
                new Account(1L, "A", BigDecimal.valueOf(100.00)),
                new Account(2L, "B", BigDecimal.valueOf(200.00))
        );
        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].ownerName").value("A"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getAccountTransactions_returnsList() throws Exception {
        Transaction tx1 = new Transaction(); tx1.setId(1L);
        Transaction tx2 = new Transaction(); tx2.setId(2L);
        List<Transaction> txs = Arrays.asList(tx1, tx2);
        when(transactionService.getTransactionsByAccountId(1L)).thenReturn(txs);

        mockMvc.perform(get("/api/accounts/1/transactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1));
    }
}