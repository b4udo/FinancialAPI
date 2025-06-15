package financial.repository;

import financial.model.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository JPA per operazioni CRUD su Transaction */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findByAccountId(Long accountId);
}
