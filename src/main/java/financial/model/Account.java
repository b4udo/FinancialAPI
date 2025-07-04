package financial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entity che rappresenta un conto finanziario */
@Entity
@Table(name = "financial_account")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String ownerName;

  @Column(precision = 10, scale = 2)
  private BigDecimal balance;
}
