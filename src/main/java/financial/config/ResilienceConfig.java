package financial.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResilienceConfig {

  @Bean
  public CircuitBreakerConfig circuitBreakerConfig() {
    return CircuitBreakerConfig.custom()
        .failureRateThreshold(50)
        .waitDurationInOpenState(Duration.ofSeconds(60))
        .slidingWindowSize(5)
        .build();
  }

  @Bean
  public RetryConfig retryConfig() {
    return RetryConfig.custom().maxAttempts(3).waitDuration(Duration.ofSeconds(2)).build();
  }
}
