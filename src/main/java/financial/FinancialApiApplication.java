package financial;

import financial.model.Account;
import financial.model.AccountType;
import financial.service.AccountService;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class FinancialApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(FinancialApiApplication.class, args);
  }

  @Bean
  @Profile("!test")
  CommandLineRunner initData(AccountService accountService) {
    return args -> {
      Account acc1 =
          accountService.createAccount(
              Account.builder()
                  .ownerName("Edoardo")
                  .balance(new BigDecimal("1000.00"))
                  .accountType(AccountType.CHECKING)
                  .build());

      Account acc2 =
          accountService.createAccount(
              Account.builder()
                  .ownerName("Andreia")
                  .balance(new BigDecimal("2000.00"))
                  .accountType(AccountType.SAVINGS)
                  .build());
    };
  }
}
