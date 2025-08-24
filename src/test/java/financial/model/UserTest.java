package financial.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user =
            User
                .builder()
                .username("testuser")
                .password("testpass")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.ROLE_USER)
                .build();
    }

    @Test
    void whenGetAuthorities_thenReturnCorrectRole() {
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertTrue(authorities.contains(new SimpleGrantedAuthority(UserRole.ROLE_USER.name())));
    }

    @Test
    void whenCheckAccountStatus_thenReturnTrue() {
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void whenGetUsername_thenReturnCorrectUsername() {
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void whenGetPassword_thenReturnCorrectPassword() {
        assertEquals("testpass", user.getPassword());
    }

    @Test
    void whenBuildUser_thenAllFieldsAreSet() {
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals(UserRole.ROLE_USER, user.getRole());
    }
}
