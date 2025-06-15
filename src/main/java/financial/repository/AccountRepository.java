package financial.repository;

import financial.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository JPA per operazioni CRUD su Account */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {}
