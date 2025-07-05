package financial.service;

import financial.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Async
    public void notifyImportantTransaction(Transaction transaction) {
        // In un'implementazione reale, qui si invierebbe una vera notifica
        // via email, SMS, o push notification
        log.info("NOTIFICA IMPORTANTE: Nuova transazione di {} EUR per {}",
                transaction.getAmount(),
                transaction.getDescription());
    }

    @Async
    public void notifyRecurringTransactionScheduled(Transaction transaction) {
        log.info("NOTIFICA: Transazione ricorrente programmata per {} - Prossima esecuzione: {}",
                transaction.getDescription(),
                transaction.getNextExecutionDate());
    }
}
