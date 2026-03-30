package com.app.quantitymeasurement.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTOs (Data Transfer Objects) used for the /auth endpoints.
 *
 * RegisterRequest  – body sent to POST /auth/register
 * LoginRequest     – body sent to POST /auth/login
 * AuthResponse     – JWT token returned to the client after successful auth
 */
public class AuthDTOs {

    // ─────────────────────────── Register ───────────────────────────

    /**
     * Body expected by POST /auth/register.
     *
     * Example JSON:
     * {
     *   "username": "alice@example.com",
     *   "password": "Secret123!",
     *   "role": "USER"
     * }
     */
    @Data
    public static class RegisterRequest {

        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        /**
         * Optional role. Defaults to "USER" if not supplied.
         * Valid values: "USER", "ADMIN"
         */
        private String role = "USER";
    }

    // ──────────────────────────── Login ─────────────────────────────

    /**
     * Body expected by POST /auth/login.
     *
     * Example JSON:
     * {
     *   "username": "alice@example.com",
     *   "password": "Secret123!"
     * }
     */
    @Data
    public static class LoginRequest {

        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;
    }

    // ─────────────────────────── Response ───────────────────────────

    /**
     * Response returned to the client after a successful register or login.
     *
     * The client must store this token and send it as:
     *   Authorization: Bearer <token>
     * on every subsequent request to protected endpoints.
     */
    @Data
    public static class AuthResponse {

        /** The signed JWT token */
        private final String token;

        /** Token type – always "Bearer" */
        private final String tokenType = "Bearer";

        /** When the token expires (ISO-8601 string for readability) */
        private final String expiresAt;

        /** The authenticated user's username */
        private final String username;

        /** The user's assigned role */
        private final String role;
    }
}
