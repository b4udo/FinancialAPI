package financial.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_consents")
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

    @Column(length = 512)
    private String ipAddress;

    @Column(length = 1024)
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        consentTimestamp = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
