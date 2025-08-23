package financial.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import financial.model.UserConsent;
import financial.service.PrivacyService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PrivacyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrivacyService privacyService;

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    public void recordConsent_Success() throws Exception {
        UserConsent consent = new UserConsent();
        consent.setId(1L);
        consent.setUserId("user123");
        consent.setConsentType("DATA_PROCESSING");
        consent.setConsented(true);
        consent.setConsentTimestamp(LocalDateTime.now());

        when(privacyService.recordConsent(eq("user123"), eq("DATA_PROCESSING"), eq(true), any(), any()))
            .thenReturn(consent);

        mockMvc
            .perform(
                post("/api/v1/privacy/consent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"userId\":\"user123\",\"consentType\":\"DATA_PROCESSING\",\"consented\":true}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("user123"))
            .andExpect(jsonPath("$.consentType").value("DATA_PROCESSING"))
            .andExpect(jsonPath("$.consented").value(true));
    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    public void getUserConsents_Success() throws Exception {
        UserConsent consent = new UserConsent();
        consent.setUserId("user123");
        consent.setConsentType("DATA_PROCESSING");
        consent.setConsented(true);

        when(privacyService.getUserConsents("user123")).thenReturn(Collections.singletonList(consent));

        mockMvc
            .perform(get("/api/v1/privacy/consent/user123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value("user123"))
            .andExpect(jsonPath("$[0].consentType").value("DATA_PROCESSING"));
    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    public void deleteUserData_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/privacy/user/user123")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    public void exportUserData_Success() throws Exception {
        UserConsent consent = new UserConsent();
        consent.setUserId("user123");
        consent.setConsentType("DATA_PROCESSING");
        consent.setConsented(true);

        Map<String, Object> userData = new HashMap<>();
        userData.put("consents", Collections.singletonList(consent));

        when(privacyService.exportUserData("user123")).thenReturn(userData);

        mockMvc
            .perform(get("/api/v1/privacy/export/user123"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.consents[0].userId").value("user123"))
            .andExpect(jsonPath("$.consents[0].consentType").value("DATA_PROCESSING"))
            .andExpect(jsonPath("$.consents[0].consented").value(true));
    }
}
