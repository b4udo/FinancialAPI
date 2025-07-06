package financial.controller;

import financial.model.TransactionCategory;
import financial.service.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
public class StatisticsController {

  private final TransactionService transactionService;

  @Autowired
  public StatisticsController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @GetMapping("/monthly")
  public ResponseEntity<Map<TransactionCategory, BigDecimal>> getMonthlyStatsByCategory(
      @RequestParam("startDate") LocalDateTime startDate,
      @RequestParam("endDate") LocalDateTime endDate) {
    Map<TransactionCategory, BigDecimal> stats =
        transactionService.getCategoryStatistics(startDate, endDate);
    return ResponseEntity.ok(stats);
  }

  @GetMapping("/transactions/search")
  public ResponseEntity<?> searchTransactions(
      @RequestParam(value = "category", required = false) TransactionCategory category,
      @RequestParam(value = "minAmount", required = false) BigDecimal minAmount,
      @RequestParam(value = "maxAmount", required = false) BigDecimal maxAmount,
      @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
      @RequestParam(value = "endDate", required = false) LocalDateTime endDate) {
    return ResponseEntity.ok(
        transactionService.searchTransactions(category, minAmount, maxAmount, startDate, endDate));
  }

  @GetMapping("/summary/annual")
  public ResponseEntity<Map<String, Object>> getAnnualSummary(@RequestParam("year") int year) {
    Map<String, Object> summary = transactionService.getAnnualSummary(year);
    return ResponseEntity.ok(summary);
  }
}
