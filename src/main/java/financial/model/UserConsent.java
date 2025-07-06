ga in italiano cosa hai fatttoepackage financial.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class UserConsent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String consentType;

    @Column(nullable = false)
    private boolean consented;

    @Column(nullable = false)
    private LocalDateTime consentTimestamp;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Column
    private String ipAddress;

    @Column
    private String userAgent;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        lastUpdated = LocalDateTime.now();
    }
}
