package financial.repository;

import financial.model.Transaction;
import financial.model.TransactionCategory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repository JPA per operazioni CRUD su Transaction */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findByAccountId(Long accountId);

  // Query per le statistiche
  List<Transaction> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

  // Query per le transazioni ricorrenti
  List<Transaction> findByIsRecurringTrueAndNextExecutionDateAfter(LocalDateTime date);

  List<Transaction> findByIsRecurringTrueAndNextExecutionDateBefore(LocalDateTime date);

  @Query(
      "SELECT t FROM Transaction t WHERE "
          + "(:category IS NULL OR t.category = :category) AND "
          + "(:minAmount IS NULL OR t.amount >= :minAmount) AND "
          + "(:maxAmount IS NULL OR t.amount <= :maxAmount) AND "
          + "(:startDate IS NULL OR t.timestamp >= :startDate) AND "
          + "(:endDate IS NULL OR t.timestamp <= :endDate)")
  List<Transaction> findByFilters(
      @Param("category") TransactionCategory category,
      @Param("minAmount") BigDecimal minAmount,
      @Param("maxAmount") BigDecimal maxAmount,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
}
