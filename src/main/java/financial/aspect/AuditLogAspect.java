package financial.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class AuditLogAspect {
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOG");

    @Around("@annotation(financial.aspect.AuditLog)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = authentication != null ? authentication.getName() : "anonymous";

        LocalDateTime timestamp = LocalDateTime.now();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Log before method execution
        auditLogger.info("AUDIT: User {} accessed {}.{} at {} with parameters: {}",
            user, className, methodName, timestamp, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            // Log after successful execution
            auditLogger.info("AUDIT: User {} successfully completed {}.{} at {}",
                user, className, methodName, LocalDateTime.now());

            return result;
        } catch (Exception e) {
            // Log if operation fails
            auditLogger.error("AUDIT: User {} failed executing {}.{} at {} with error: {}",
                user, className, methodName, LocalDateTime.now(), e.getMessage());
            throw e;
        }
    }
}
