package financial.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import financial.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction =
            Transaction
                .builder()
                .amount(new BigDecimal("1000.00"))
                .description("Test Transaction")
                .nextExecutionDate(LocalDateTime.now().plusDays(7))
                .build();
    }

    @Test
    void whenNotifyImportantTransaction_thenNoException() {
        assertDoesNotThrow(() -> notificationService.notifyImportantTransaction(transaction));
    }

    @Test
    void whenNotifyRecurringTransactionScheduled_thenNoException() {
        assertDoesNotThrow(() -> notificationService.notifyRecurringTransactionScheduled(transaction));
    }
}
