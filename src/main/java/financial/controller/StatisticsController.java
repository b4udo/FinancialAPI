package financial.controller;

import financial.model.Transaction;
import financial.model.TransactionCategory;
import financial.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final TransactionService transactionService;

    @GetMapping("/monthly")
    public ResponseEntity<Map<TransactionCategory, BigDecimal>> getMonthlyStatsByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.getCategoryStatistics(startDate, endDate));
    }

    @GetMapping("/transactions/search")
    public ResponseEntity<List<Transaction>> searchTransactions(
            @RequestParam(required = false) TransactionCategory category,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.searchTransactions(category, minAmount, maxAmount, startDate, endDate));
    }

    @GetMapping("/recurring")
    public ResponseEntity<List<Transaction>> getUpcomingRecurringTransactions() {
        return ResponseEntity.ok(transactionService.getUpcomingRecurringTransactions());
    }

    @GetMapping("/summary/annual")
    public ResponseEntity<Map<String, Object>> getAnnualSummary(
            @RequestParam int year) {
        return ResponseEntity.ok(transactionService.getAnnualSummary(year));
    }
}
