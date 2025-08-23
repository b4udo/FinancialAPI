package financial.repository;

import financial.model.UserConsent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConsentRepository extends JpaRepository<UserConsent, Long> {
    List<UserConsent> findByUserId(String userId);

    boolean existsByUserIdAndConsentTypeAndConsented(String userId, String consentType, boolean consented);
}
