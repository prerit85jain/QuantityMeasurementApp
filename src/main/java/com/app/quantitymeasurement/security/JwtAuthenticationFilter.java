package com.app.quantitymeasurement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter – runs once per HTTP request.
 *
 * ┌───────────────────────────────────────────────────────┐
 * │  Flow for EVERY request:                              │
 * │                                                       │
 * │  1. Read the "Authorization" header                   │
 * │  2. If it starts with "Bearer ", extract the token    │
 * │  3. Extract the username from the token               │
 * │  4. Load the user from DB (UserDetailsService)        │
 * │  5. Validate token (signature + expiry + username)    │
 * │  6. If valid → set authentication in SecurityContext  │
 * │  7. Continue to the next filter / controller          │
 * │                                                       │
 * │  If the header is missing or the token is invalid,    │
 * │  we simply do NOT set authentication – Spring         │
 * │  Security will then return 401 for protected routes.  │
 * └───────────────────────────────────────────────────────┘
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // ── Step 1: Read the Authorization header ──
        final String authHeader = request.getHeader("Authorization");

        // If there is no "Bearer " token, skip this filter (no authentication set)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 2: Extract the JWT string (remove "Bearer " prefix) ──
        final String jwt = authHeader.substring(7);

        // ── Step 3: Extract username from token ──
        final String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Invalid token – continue without setting auth (will result in 401)
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 4 & 5: Load user and validate ──
        // Only proceed if we got a username AND the SecurityContext is not already set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                // ── Step 6: Create Authentication object and put it in the SecurityContext ──
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                        // no credentials needed after validation
                                userDetails.getAuthorities() // roles / authorities
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // This tells Spring Security "this request is authenticated"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // ── Step 7: Pass to the next filter/controller ──
        filterChain.doFilter(request, response);
    }
}
