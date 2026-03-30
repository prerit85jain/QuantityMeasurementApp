package com.app.quantitymeasurement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller for OAuth2 and public authentication endpoints.
 * Provides clean API endpoints for OAuth2 login flow.
 */
@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    /**
     * Initiates Google OAuth2 login.
     * Returns a message with the OAuth2 authorization URL.
     */
    @GetMapping("/login/google")
    public ResponseEntity<Map<String, Object>> initiateGoogleLogin() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Redirecting to Google OAuth2...");
        response.put("authorizationUrl", "/oauth2/authorization/google");
        response.put("instructions", "Visit the authorization URL in a browser to authenticate with Google");
        
        return ResponseEntity.ok(response);
    }
}
