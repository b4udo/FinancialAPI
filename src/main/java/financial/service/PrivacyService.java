package financial.service;

import financial.model.UserConsent;
import financial.repository.UserConsentRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrivacyService {

    @Autowired
    private UserConsentRepository consentRepository;

    @Autowired
    private TextEncryptor textEncryptor;

    @Transactional
    public UserConsent recordConsent(
        String userId,
        String consentType,
        boolean consented,
        String ipAddress,
        String userAgent
    ) {
        UserConsent consent = new UserConsent();
        consent.setUserId(userId);
        consent.setConsentType(consentType);
        consent.setConsented(consented);
        consent.setConsentTimestamp(LocalDateTime.now());
        consent.setIpAddress(encryptData(ipAddress));
        consent.setUserAgent(userAgent);

        return consentRepository.save(consent);
    }

    public boolean hasUserConsented(String userId, String consentType) {
        return consentRepository.existsByUserIdAndConsentTypeAndConsented(userId, consentType, true);
    }

    public List<UserConsent> getUserConsents(String userId) {
        return consentRepository.findByUserId(userId);
    }

    public String encryptData(String data) {
        if (data == null) return null;
        return textEncryptor.encrypt(data);
    }

    public String decryptData(String encryptedData) {
        if (encryptedData == null) return null;
        return textEncryptor.decrypt(encryptedData);
    }

    public void deleteUserData(String userId) {
        // Implement GDPR right to be forgotten
        consentRepository.findByUserId(userId).forEach(consent -> consentRepository.delete(consent));
    }

    public Map<String, Object> exportUserData(String userId) {
        Map<String, Object> userData = new HashMap<>();

        // Raccogliere tutti i consensi dell'utente
        List<UserConsent> consents = consentRepository.findByUserId(userId);
        userData.put("consents", consents);
        return userData;
    }
}
