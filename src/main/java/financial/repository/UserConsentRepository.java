a in italiano cosa hai fatto e a cosa servegpackage financial.repository;

import financial.model.UserConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserConsentRepository extends JpaRepository<UserConsent, Long> {
    List<UserConsent> findByUserId(String userId);
    boolean existsByUserIdAndConsentTypeAndConsented(String userId, String consentType, boolean consented);
}
