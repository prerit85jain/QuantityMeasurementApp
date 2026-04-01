package com.app.quantitymeasurement.security;

import com.app.quantitymeasurement.model.User;
import com.app.quantitymeasurement.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String provider = "GOOGLE";
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
        }

        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");
        if (providerId == null) {
            Object id = oAuth2User.getAttribute("id");
            providerId = id != null ? id.toString() : email;
        }
        if (email == null) {
            email = providerId;
        }

        final String finalProvider = provider;
        final String finalProviderId = providerId;
        final String finalEmail = email;

        User user = userRepository.findByProviderAndProviderId(finalProvider, finalProviderId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(finalEmail)
                        .password(null)
                        .role("USER")
                        .provider(finalProvider)
                        .providerId(finalProviderId)
                        .build()));

        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password("")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .build();

        String token = jwtService.generateToken(userDetails);
        String redirectUrl = frontendUrl.replaceAll("/$", "") + "/oauth2/callback?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
