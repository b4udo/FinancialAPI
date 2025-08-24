package financial.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import financial.model.User;
import financial.model.UserRole;
import financial.model.dto.AuthRequest;
import financial.repository.UserRepository;
import financial.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRequest validRequest;
    private User validUser;

    @BeforeEach
    void setUp() {
        validRequest = new AuthRequest("testuser", "password123");
        validUser = User.builder().username("testuser").password("encoded_password").role(UserRole.ROLE_USER).build();
    }

    @Test
    void whenRegisterWithNewUsername_thenReturnToken() throws Exception {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        when(jwtService.generateToken(any())).thenReturn("test.jwt.token");

        mockMvc
            .perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("test.jwt.token"));
    }

    @Test
    void whenRegisterWithExistingUsername_thenReturnBadRequest() throws Exception {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        mockMvc
            .perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Username already exists"));
    }

    @Test
    void whenLoginWithValidCredentials_thenReturnToken() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(validUser));
        when(jwtService.generateToken(any())).thenReturn("test.jwt.token");

        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("test.jwt.token"));
    }
}
