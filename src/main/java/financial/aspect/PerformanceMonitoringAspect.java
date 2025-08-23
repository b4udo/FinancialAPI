package financial.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PerformanceMonitoringAspect {

  private final MeterRegistry meterRegistry;

  @Around(
      "@annotation(org.springframework.web.bind.annotation.RequestMapping) || "
          + "@annotation(org.springframework.web.bind.annotation.GetMapping) || "
          + "@annotation(org.springframework.web.bind.annotation.PostMapping) || "
          + "@annotation(org.springframework.web.bind.annotation.PutMapping) || "
          + "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
  public Object measureEndpointPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    Timer timer =
        Timer.builder("endpoint.performance")
            .tag("method", methodName)
            .description("Performance measurement for endpoints")
            .register(meterRegistry);

    long startTime = System.nanoTime();
    try {
      return joinPoint.proceed();
    } finally {
      long endTime = System.nanoTime();
      timer.record(endTime - startTime, TimeUnit.NANOSECONDS);

      log.info(
          "Method {} execution time: {} ms",
          methodName,
          TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
    }
  }
}
