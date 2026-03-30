package com.app.quantitymeasurement.config;

import com.app.quantitymeasurement.security.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * OAuth2 authentication success handler.
 * Generates a JWT token after successful Google/GitHub login and redirects with the token.
 */
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    private final JwtService jwtService;

    public OAuth2AuthenticationSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("OAuth2 authentication success handler triggered");

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        CustomOAuth2UserPrincipal principal = (CustomOAuth2UserPrincipal) oauth2User;

        String role = principal.getRole() != null ? principal.getRole() : "USER";
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        // Build Spring Security UserDetails to pass to JwtService
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(principal.getUsername())
                .password("")
                .authorities(List.of(new SimpleGrantedAuthority(roleWithPrefix)))
                .build();

        String token = jwtService.generateToken(userDetails);

        log.info("JWT token generated for OAuth2 user: {}", principal.getUsername());

        // Redirect to the app with the token as a query param (frontend picks it up)
        String redirectUrl = "/api/v1/quantities?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}
