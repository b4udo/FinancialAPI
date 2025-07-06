package financial.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime timestamp;

  @Column(precision = 10, scale = 2)
  private BigDecimal amount;

  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id")
  private Account account;

  @Enumerated(EnumType.STRING)
  private TransactionCategory category;

  private boolean isRecurring;

  @Column(name = "recurrence_period")
  private String recurrencePeriod; // DAILY, WEEKLY, MONTHLY, YEARLY

  @Column(name = "next_execution")
  private LocalDateTime nextExecutionDate;

  private boolean isImportant;

  @Column(name = "notification_sent")
  private boolean notificationSent;

  @PrePersist
  protected void onCreate() {
    if (timestamp == null) {
      timestamp = LocalDateTime.now();
    }
    if (amount.compareTo(new BigDecimal("1000")) > 0) {
      isImportant = true;
    }
  }
}
