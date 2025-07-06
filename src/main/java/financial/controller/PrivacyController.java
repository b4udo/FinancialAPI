package financial.controller;

import financial.aspect.AuditLog;
import financial.model.UserConsent;
import financial.service.PrivacyService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/privacy")
public class PrivacyController {

    @Autowired
    private PrivacyService privacyService;

    @PostMapping("/consent")
    @AuditLog(operation = "RECORD_CONSENT")
    public ResponseEntity<UserConsent> recordConsent(
        @RequestBody Map<String, Object> consentRequest,
        HttpServletRequest request
    ) {
        String userId = (String) consentRequest.get("userId");
        String consentType = (String) consentRequest.get("consentType");
        boolean consented = (boolean) consentRequest.get("consented");

        UserConsent consent = privacyService.recordConsent(
            userId,
            consentType,
            consented,
            request.getRemoteAddr(),
            request.getHeader("User-Agent")
        );

        return ResponseEntity.ok(consent);
    }

    @GetMapping("/consent/{userId}")
    @AuditLog(operation = "GET_USER_CONSENTS")
    public ResponseEntity<List<UserConsent>> getUserConsents(@PathVariable String userId) {
        return ResponseEntity.ok(privacyService.getUserConsents(userId));
    }

    @DeleteMapping("/user/{userId}")
    @AuditLog(operation = "DELETE_USER_DATA")
    public ResponseEntity<Void> deleteUserData(@PathVariable String userId) {
        privacyService.deleteUserData(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/export/{userId}")
    @AuditLog(operation = "EXPORT_USER_DATA")
    public ResponseEntity<Map<String, Object>> exportUserData(@PathVariable String userId) {
        Map<String, Object> userData = privacyService.exportUserData(userId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(userData);
    }
}
