package com.app.quantitymeasurement.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JwtServiceTest
 *
 * Unit tests for JWT generation and validation.
 * Does not load the full Spring context (fast).
 */
class JwtServiceTest {

    private JwtService jwtService;

    // Base64 of a 32-byte key = "myTestSecretKeyForJWTThatIsAtLeast256BitsLong"
    private static final String TEST_SECRET =
        "bXlUZXN0U2VjcmV0S2V5Rm9ySldUVGhhdElzQXRMZWFzdDI1NkJpdHNMb25n";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86_400_000L); // 24 h
    }

    private UserDetails userDetails(String username, String role) {
        return User.builder()
                .username(username)
                .password("hashed")
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .build();
    }

    @Test
    @DisplayName("generateToken returns a non-empty string")
    void generateToken_notEmpty() {
        String token = jwtService.generateToken(userDetails("alice", "ROLE_USER"));
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("extractUsername returns correct subject")
    void extractUsername_correct() {
        UserDetails ud = userDetails("alice@example.com", "ROLE_USER");
        String token = jwtService.generateToken(ud);
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("extractRole returns correct role claim")
    void extractRole_correct() {
        String token = jwtService.generateToken(userDetails("bob", "ROLE_ADMIN"));
        assertThat(jwtService.extractRole(token)).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("isTokenValid returns true for matching user before expiry")
    void isTokenValid_true() {
        UserDetails ud = userDetails("carol", "ROLE_USER");
        String token = jwtService.generateToken(ud);
        assertThat(jwtService.isTokenValid(token, ud)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid returns false for different username")
    void isTokenValid_wrongUser() {
        UserDetails alice = userDetails("alice", "ROLE_USER");
        UserDetails bob   = userDetails("bob",   "ROLE_USER");
        String tokenForAlice = jwtService.generateToken(alice);
        assertThat(jwtService.isTokenValid(tokenForAlice, bob)).isFalse();
    }

    @Test
    @DisplayName("Expired token throws JwtException")
    void expiredToken_throwsException() {
        // Set expiration to -1 ms so the token is immediately expired
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1L);
        UserDetails ud = userDetails("dave", "ROLE_USER");
        String expiredToken = jwtService.generateToken(ud);

        // Parsing an expired token should throw
        assertThatThrownBy(() -> jwtService.extractUsername(expiredToken))
                .isInstanceOf(io.jsonwebtoken.JwtException.class);
    }
}
