package com.app.quantitymeasurement.security;

import com.app.quantitymeasurement.model.User;
import com.app.quantitymeasurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CustomUserDetailsService
 *
 * Spring Security calls this whenever it needs to load a user during:
 *   (a) username+password login (AuthenticationManager)
 *   (b) JWT filter (JwtAuthenticationFilter) to verify the token's subject
 *
 * It bridges our User entity → Spring Security's UserDetails interface.
 *
 * The role stored in the DB (e.g. "USER") is prefixed with "ROLE_"
 * to match Spring Security's convention (e.g. "ROLE_USER").
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load a user by username.
     *
     * @param username the email / login stored in the DB
     * @return UserDetails used by Spring Security internally
     * @throws UsernameNotFoundException if no such user exists
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        // Convert our role string → Spring Security GrantedAuthority
        // DB stores "USER" or "ADMIN", Spring expects "ROLE_USER" or "ROLE_ADMIN"
        String roleWithPrefix = user.getRole().startsWith("ROLE_")
                ? user.getRole()
                : "ROLE_" + user.getRole();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .authorities(List.of(new SimpleGrantedAuthority(roleWithPrefix)))
                .build();
    }
}
