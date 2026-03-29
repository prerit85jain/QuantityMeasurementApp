package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.AuthDTOs.AuthResponse;
import com.app.quantitymeasurement.model.AuthDTOs.LoginRequest;
import com.app.quantitymeasurement.model.AuthDTOs.RegisterRequest;
import com.app.quantitymeasurement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController – public endpoints for registration and login.
 *
 * No JWT required to call these endpoints (configured in SecurityConfig).
 *
 * Endpoints:
 *   POST /auth/register  → create account, returns JWT
 *   POST /auth/login     → verify credentials, returns JWT
 *
 * The returned JWT must be sent as:
 *   Authorization: Bearer <token>
 * on all subsequent requests to /api/v1/quantities/**
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login to obtain a JWT token")
public class AuthController {

    private final AuthService authService;

    // ─────────────────────────── Register ───────────────────────────

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = """
            Creates a new user account and returns a signed JWT token.
            
            The token must be sent as **Authorization: Bearer <token>** on every
            protected request (e.g. POST /api/v1/quantities/compare).
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                {
                  "username": "alice@example.com",
                  "password": "Secret123!",
                  "role": "USER"
                }
                """))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "User registered, JWT returned"),
            @ApiResponse(responseCode = "400", description = "Username already taken or invalid input")
        }
    )
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ──────────────────────────── Login ─────────────────────────────

    @PostMapping("/login")
    @Operation(
        summary = "Login with username and password",
        description = """
            Authenticates the user and returns a signed JWT token.
            
            Use this token in the **Authorization: Bearer <token>** header
            for all protected API calls.
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                {
                  "username": "alice@example.com",
                  "password": "Secret123!"
                }
                """))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
        }
    )
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    // ──────────────────────── OAuth2 info ───────────────────────────

    @GetMapping("/oauth2-info")
    @Operation(
        summary = "OAuth2 login URLs",
        description = """
            Returns the OAuth2 social login URLs.
            
            Visit these URLs in your browser to start the social login flow:
            - Google: /oauth2/authorization/google
            - GitHub: /oauth2/authorization/github
            
            After successful login, you will be redirected with a JWT token.
            """
    )
    public ResponseEntity<?> oauth2Info() {
        return ResponseEntity.ok(java.util.Map.of(
            "google_login_url", "/oauth2/authorization/google",
            "github_login_url", "/oauth2/authorization/github",
            "note", "Open these URLs in a browser. After login you will be redirected with ?token=<JWT>"
        ));
    }
}
