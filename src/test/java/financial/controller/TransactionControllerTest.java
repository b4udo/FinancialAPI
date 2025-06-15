package financial.controller;

import financial.model.Transaction;
import financial.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testGetAll() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(Arrays.asList(new Transaction(), new Transaction()));
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(transaction));

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(transactionService).deleteTransaction(1L);

        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());
    }
}
