package com.app.quantitymeasurement.security;

import com.app.quantitymeasurement.model.User;
import com.app.quantitymeasurement.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * OAuth2LoginSuccessHandler
 *
 * ┌──────────────────────────────────────────────────────────┐
 * │  What happens after a user logs in via Google/GitHub?   │
 * │                                                         │
 * │  1. User clicks "Login with Google"                     │
 * │  2. Browser redirects to Google's consent screen        │
 * │  3. Google redirects back to /login/oauth2/code/google  │
 * │  4. Spring Security exchanges the code for user info    │
 * │  5. THIS handler runs with the authenticated OAuth2User │
 * │  6. We create/find the user in our DB                   │
 * │  7. We generate a JWT token for them                    │
 * │  8. We redirect them to /api/v1/quantities with token   │
 * └──────────────────────────────────────────────────────────┘
 *
 * The token is appended as a query param (?token=...) so a
 * frontend / Swagger client can pick it up immediately.
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // ── Determine the provider (Google vs GitHub) ──
        // The registration ID is available via the OAuth2AuthenticationToken
        String provider = "GOOGLE"; // default – we refine below if needed
        if (authentication instanceof
                org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauthToken) {
            provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
        }

        // ── Extract attributes from the OAuth2 provider ──
        String email      = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub"); // Google uses "sub"
        if (providerId == null) {
            // GitHub uses "id" (numeric) instead of "sub"
            Object id = oAuth2User.getAttribute("id");
            providerId = id != null ? id.toString() : email;
        }
        if (email == null) email = providerId; // fallback

        final String finalProvider   = provider;
        final String finalProviderId = providerId;
        final String finalEmail      = email;

        // ── Find or create the user in our database ──
        User user = userRepository.findByProviderAndProviderId(finalProvider, finalProviderId)
                .orElseGet(() -> {
                    // First-time OAuth2 login → persist the user
                    User newUser = User.builder()
                            .username(finalEmail)
                            .password(null)          // no password for OAuth2 users
                            .role("USER")
                            .provider(finalProvider)
                            .providerId(finalProviderId)
                            .build();
                    return userRepository.save(newUser);
                });

        // ── Build Spring Security UserDetails and generate JWT ──
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password("")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .build();

        String token = jwtService.generateToken(userDetails);

        // ── Redirect to the API root with the JWT as a query param ──
        // In a real frontend you would redirect to your SPA's callback URL
        String redirectUrl = "/api/v1/quantities?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
