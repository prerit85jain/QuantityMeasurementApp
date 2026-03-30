package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.model.AuthDTOs.AuthResponse;
import com.app.quantitymeasurement.model.AuthDTOs.LoginRequest;
import com.app.quantitymeasurement.model.AuthDTOs.RegisterRequest;
import com.app.quantitymeasurement.model.User;
import com.app.quantitymeasurement.repository.UserRepository;
import com.app.quantitymeasurement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AuthService – business logic for registration and login.
 *
 * Register flow:
 *  1. Check username not already taken
 *  2. Hash the password with BCrypt
 *  3. Save User entity to DB
 *  4. Generate JWT
 *  5. Return AuthResponse with token
 *
 * Login flow:
 *  1. Call AuthenticationManager.authenticate()
 *     → This calls UserDetailsService + BCrypt internally
 *     → Throws BadCredentialsException if wrong password
 *  2. Load user from DB to get the role
 *  3. Generate JWT
 *  4. Return AuthResponse with token
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ─────────────────────────── Register ───────────────────────────

    /**
     * Register a new local user.
     *
     * @param request contains username, plaintext password, optional role
     * @return AuthResponse with signed JWT
     * @throws IllegalArgumentException if username already exists
     */
    public AuthResponse register(RegisterRequest request) {
        // Guard: username must be unique
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException(
                    "Username already exists: " + request.getUsername());
        }

        // Normalise role (accept "USER", "ADMIN", "ROLE_USER", "ROLE_ADMIN")
        String role = request.getRole() == null ? "USER" : request.getRole()
                .replace("ROLE_", "").toUpperCase();

        // Persist the new user with a BCrypt-hashed password
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // hash!
                .role(role)
                .provider("LOCAL")
                .build();
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    // ──────────────────────────── Login ─────────────────────────────

    /**
     * Authenticate a local user and return a JWT.
     *
     * AuthenticationManager.authenticate() will:
     *  - call UserDetailsService.loadUserByUsername()
     *  - verify the password with BCryptPasswordEncoder
     *  - throw AuthenticationException on failure
     *
     * @param request contains username and plaintext password
     * @return AuthResponse with signed JWT
     */
    public AuthResponse login(LoginRequest request) {
        // Spring Security verifies credentials here; throws on failure
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Credentials verified → load user and generate token
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found: " + request.getUsername()));

        return buildAuthResponse(user);
    }

    // ─────────────────────────── Helpers ────────────────────────────

    /** Build the JWT and wrap it in an AuthResponse */
    private AuthResponse buildAuthResponse(User user) {
        // Build Spring Security UserDetails to pass to JwtService
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .build();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                token,
                jwtService.extractExpirationString(token),
                user.getUsername(),
                user.getRole()
        );
    }
}
