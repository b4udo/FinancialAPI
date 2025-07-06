package financial.service;

import financial.model.UserConsent;
import financial.repository.UserConsentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrivacyServiceTest {

    @Mock
    private UserConsentRepository consentRepository;

    @Mock
    private TextEncryptor textEncryptor;

    @InjectMocks
    private PrivacyService privacyService;

    @Test
    void recordConsent_Success() {
        // Arrange
        String userId = "testUser";
        String consentType = "DATA_PROCESSING";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";

        when(textEncryptor.encrypt(ipAddress)).thenReturn("encrypted_ip");
        when(consentRepository.save(any(UserConsent.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserConsent result = privacyService.recordConsent(userId, consentType, true, ipAddress, userAgent);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(consentType, result.getConsentType());
        assertTrue(result.isConsented());
        verify(textEncryptor).encrypt(ipAddress);
        verify(consentRepository).save(any(UserConsent.class));
    }

    @Test
    void hasUserConsented_ReturnsTrue() {
        // Arrange
        String userId = "testUser";
        String consentType = "DATA_PROCESSING";

        when(consentRepository.existsByUserIdAndConsentTypeAndConsented(userId, consentType, true))
            .thenReturn(true);

        // Act
        boolean result = privacyService.hasUserConsented(userId, consentType);

        // Assert
        assertTrue(result);
        verify(consentRepository).existsByUserIdAndConsentTypeAndConsented(userId, consentType, true);
    }

    @Test
    void getUserConsents_ReturnsListOfConsents() {
        // Arrange
        String userId = "testUser";
        UserConsent consent = new UserConsent();
        consent.setUserId(userId);

        when(consentRepository.findByUserId(userId))
            .thenReturn(Arrays.asList(consent));

        // Act
        List<UserConsent> results = privacyService.getUserConsents(userId);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(userId, results.get(0).getUserId());
        verify(consentRepository).findByUserId(userId);
    }

    @Test
    void encryptData_Success() {
        // Arrange
        String data = "sensitive_data";
        String encrypted = "encrypted_data";
        when(textEncryptor.encrypt(data)).thenReturn(encrypted);

        // Act
        String result = privacyService.encryptData(data);

        // Assert
        assertEquals(encrypted, result);
        verify(textEncryptor).encrypt(data);
    }

    @Test
    void decryptData_Success() {
        // Arrange
        String encrypted = "encrypted_data";
        String decrypted = "sensitive_data";
        when(textEncryptor.decrypt(encrypted)).thenReturn(decrypted);

        // Act
        String result = privacyService.decryptData(encrypted);

        // Assert
        assertEquals(decrypted, result);
        verify(textEncryptor).decrypt(encrypted);
    }

    @Test
    void deleteUserData_Success() {
        // Arrange
        String userId = "testUser";
        UserConsent consent = new UserConsent();
        consent.setUserId(userId);

        when(consentRepository.findByUserId(userId))
            .thenReturn(Arrays.asList(consent));

        // Act
        privacyService.deleteUserData(userId);

        // Assert
        verify(consentRepository).findByUserId(userId);
        verify(consentRepository).delete(consent);
    }
}
