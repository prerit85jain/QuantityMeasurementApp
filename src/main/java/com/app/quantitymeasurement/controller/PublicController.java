package com.app.quantitymeasurement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Public controller for health check and welcome message.
 * This endpoint is accessible without authentication for demonstration purposes.
 */
@RestController
@RequestMapping("/api")
public class PublicController {

    /**
     * Health check endpoint - accessible without authentication
     * Use this to verify the application is running in browser
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("message", "Quantity Measurement API is running!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Welcome endpoint - accessible without authentication
     */
    @GetMapping("/welcome")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Welcome to Quantity Measurement API");
        response.put("version", "1.0.0");
        response.put("endpoints", new String[]{
            "POST /api/auth/register - Register new user",
            "POST /api/auth/login - Login",
            "POST /api/measurements/convert - Convert units (requires JWT)",
            "POST /api/measurements/compare - Compare quantities (requires JWT)",
            "GET /api/measurements/history - Get history (requires JWT)"
        });
        response.put("oauth2", "GET /oauth2/authorize/google - Google OAuth2 Login");
        return ResponseEntity.ok(response);
    }
}
