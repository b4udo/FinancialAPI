package financial.service;

import financial.exception.AccountNotFoundException;
import financial.model.Account;
import financial.repository.AccountRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** Service che incapsula la logica di business per gli Account */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Account createAccount(Account account) {
        account.setId(null);
        return accountRepository.save(account);
    }

    /**
     * Aggiorna un account esistente. Lancia AccountNotFoundException se l'account con l'id dato non
     * esiste.
     *
     *
     */
    @Transactional
    public Account updateAccount(Long id, Account payload) {
        Account existing = accountRepository
            .findById(id)
            .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));

        existing.setOwnerName(payload.getOwnerName());
        existing.setBalance(payload.getBalance());
        existing.setAccountType(payload.getAccountType());

        return accountRepository.save(existing);
    }

    /**
     * Elimina un account, lanciando AccountNotFoundException se non esiste.
     *
     *
     */
    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }
}
