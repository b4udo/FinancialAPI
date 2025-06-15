package financial.controller;

import financial.model.Account;
import financial.model.Transaction;
import financial.service.AccountService;
import financial.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * FinancialController è responsabile di ricevere e gestire le richieste HTTP
 */
@RestController
@RequestMapping("/api")
public class FinancialController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public FinancialController(AccountService accountService,
                               TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    // Recupera tutti i conti
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // Recupera un conto per ID
    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable Long id) {
        List<Transaction> txs = transactionService.getTransactionsByAccountId(id);
        return ResponseEntity.ok(txs);
    }

    // Crea un nuovo conto
    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account created = accountService.createAccount(account);
        return ResponseEntity.created(URI.create("/api/accounts/" + created.getId()))
                .body(created);
    }

    // Elimina un conto
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}

