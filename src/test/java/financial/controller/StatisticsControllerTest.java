package financial.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import financial.config.JacksonConfig;
import financial.model.Transaction;
import financial.model.TransactionCategory;
import financial.security.JwtAuthenticationFilter;
import financial.security.JwtService;
import financial.security.SecurityConfig;
import financial.service.TransactionService;
import financial.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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

@WebMvcTest(StatisticsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ SecurityConfig.class, JacksonConfig.class })
@ActiveProfiles("test")
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getMonthlyStatsByCategory_Success() throws Exception {
        Map<TransactionCategory, BigDecimal> stats = new HashMap<>();
        stats.put(TransactionCategory.BILLS, new BigDecimal("100.00"));
        stats.put(TransactionCategory.GROCERIES, new BigDecimal("200.00"));

        when(transactionService.getCategoryStatistics(any(), any())).thenReturn(stats);

        mockMvc
            .perform(
                get("/api/v1/statistics/monthly")
                    .param("startDate", "2025-01-01T00:00:00")
                    .param("endDate", "2025-12-31T23:59:59")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.['BILLS']").value("100.00"))
            .andExpect(jsonPath("$.['GROCERIES']").value("200.00"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchTransactions_Success() throws Exception {
        List<Transaction> transactions = Collections.singletonList(
            Transaction
                .builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .category(TransactionCategory.BILLS)
                .description("Test Bill")
                .timestamp(LocalDateTime.now())
                .build()
        );

        when(transactionService.searchTransactions(any(), any(), any(), any(), any())).thenReturn(transactions);

        mockMvc
            .perform(
                get("/api/v1/statistics/transactions/search")
                    .param("category", "BILLS")
                    .param("minAmount", "50.00")
                    .param("maxAmount", "150.00")
                    .param("startDate", "2025-01-01T00:00:00")
                    .param("endDate", "2025-12-31T23:59:59")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].amount").value("100.00"))
            .andExpect(jsonPath("$[0].category").value("BILLS"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAnnualSummary_Success() throws Exception {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalTransactions", 2);
        summary.put("totalAmount", new BigDecimal("300.00"));

        Map<TransactionCategory, BigDecimal> breakdown = new HashMap<>();
        breakdown.put(TransactionCategory.BILLS, new BigDecimal("100.00"));
        breakdown.put(TransactionCategory.GROCERIES, new BigDecimal("200.00"));
        summary.put("categoryBreakdown", breakdown);

        when(transactionService.getAnnualSummary(2025)).thenReturn(summary);

        mockMvc
            .perform(
                get("/api/v1/statistics/summary/annual").param("year", "2025").contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalTransactions").value(2))
            .andExpect(jsonPath("$.totalAmount").value("300.00"))
            .andExpect(jsonPath("$.categoryBreakdown.BILLS").value("100.00"))
            .andExpect(jsonPath("$.categoryBreakdown.GROCERIES").value("200.00"));
    }
}
