package financial.controller;

import financial.model.Transaction;
import financial.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testGetAll() throws Exception {
        Transaction tx1 = Transaction.builder()
                .id(1L)
                .timestamp(LocalDateTime.of(2025, 6, 15, 11, 0))
                .amount(BigDecimal.valueOf(150.00))
                .description("Test1")
                .build();
        Transaction tx2 = Transaction.builder()
                .id(2L)
                .timestamp(LocalDateTime.of(2025, 6, 15, 12, 0))
                .amount(BigDecimal.valueOf(200.00))
                .description("Test2")
                .build();
        List<Transaction> list = Arrays.asList(tx1, tx2);
        when(transactionService.getAllTransactions()).thenReturn(list);

        mockMvc.perform(get("/api/transactions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].amount").value(200.00));
    }

    @Test
    void testGetById() throws Exception {
        Transaction tx = Transaction.builder()
                .id(1L)
                .timestamp(LocalDateTime.of(2025, 6, 15, 11, 0))
                .amount(BigDecimal.valueOf(100.00))
                .description("TestGet")
                .build();
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(tx));

        mockMvc.perform(get("/api/transactions/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("TestGet"));
    }

    @Test
    void testDelete() throws Exception {
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());
    }
}