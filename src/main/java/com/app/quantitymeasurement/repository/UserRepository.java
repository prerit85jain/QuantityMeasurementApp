package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for the User entity.
 *
 * Spring Data JPA automatically implements all CRUD methods.
 * We only define the two custom finders we need.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their unique username/email.
     * Used during login and by UserDetailsService.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by their OAuth2 provider + provider-specific ID.
     * Used by the OAuth2 success handler to detect returning social-login users.
     *
     * @param provider   "GOOGLE" or "GITHUB"
     * @param providerId the unique ID from the OAuth2 provider
     */
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
