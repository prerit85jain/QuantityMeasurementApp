package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.AuthDTOs.LoginRequest;
import com.app.quantitymeasurement.model.AuthDTOs.RegisterRequest;
import com.app.quantitymeasurement.model.User;
import com.app.quantitymeasurement.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthControllerTest
 *
 * Integration tests covering:
 *   - User registration (happy path + duplicate username)
 *   - User login (happy path + wrong password)
 *   - Accessing a protected endpoint with and without JWT
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    // ─────────────────────────── Register ───────────────────────────

    @Test
    @DisplayName("POST /auth/register → 200 and token returned")
    void register_success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("bob@example.com");
        req.setPassword("Pass1234!");

        MvcResult result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.username").value("bob@example.com"))
            .andReturn();

        // Verify user was persisted
        assertThat(userRepository.findByUsername("bob@example.com")).isPresent();
    }

    @Test
    @DisplayName("POST /auth/register duplicate → 400")
    void register_duplicate_username() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("duplicate@example.com");
        req.setPassword("Pass1234!");

        // First registration succeeds
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());

        // Second registration with same username → 400
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    // ──────────────────────────── Login ─────────────────────────────

    @Test
    @DisplayName("POST /auth/login → 200 and token returned")
    void login_success() throws Exception {
        // Register first
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("carol@example.com");
        reg.setPassword("Pass1234!");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
            .andExpect(status().isOk());

        // Then login
        LoginRequest login = new LoginRequest();
        login.setUsername("carol@example.com");
        login.setPassword("Pass1234!");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("POST /auth/login wrong password → 401")
    void login_wrongPassword() throws Exception {
        // Register first
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("dave@example.com");
        reg.setPassword("CorrectPass!");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
            .andExpect(status().isOk());

        // Login with wrong password
        LoginRequest login = new LoginRequest();
        login.setUsername("dave@example.com");
        login.setPassword("WrongPass!");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isUnauthorized());
    }

    // ─────────────────── Protected endpoint access ──────────────────

    @Test
    @DisplayName("GET /api/v1/quantities/all without token → 401 or 403")
    void protectedEndpoint_noToken_returns401or403() throws Exception {
        mockMvc.perform(get("/api/v1/quantities/all"))
            .andExpect(result ->
                assertThat(result.getResponse().getStatus()).isIn(401, 403));
    }

    @Test
    @DisplayName("GET /api/v1/quantities/all with valid token → not 401")
    void protectedEndpoint_withToken_succeeds() throws Exception {
        // Register + login to get a token
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("eve@example.com");
        reg.setPassword("Pass1234!");
        reg.setRole("ADMIN"); // need ADMIN to call /all

        MvcResult regResult = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
            .andExpect(status().isOk())
            .andReturn();

        String body = regResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(body).get("token").asText();

        // Use the token
        mockMvc.perform(get("/api/v1/quantities/all")
                .header("Authorization", "Bearer " + token))
            .andExpect(result ->
                assertThat(result.getResponse().getStatus()).isNotEqualTo(401));
    }
}
