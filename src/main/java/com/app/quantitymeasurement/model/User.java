package com.app.quantitymeasurement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity stored in the database.
 *
 * Holds credentials for local (username + password) login
 * AND an optional OAuth2 provider ID for Google / GitHub login.
 *
 * Fields:
 *  - id           : primary key (auto-generated)
 *  - username     : unique login name / email
 *  - password     : BCrypt-hashed password (null when OAuth2-only user)
 *  - role         : e.g. "ROLE_USER" or "ROLE_ADMIN"
 *  - provider     : "LOCAL", "GOOGLE", or "GITHUB"
 *  - providerId   : the unique ID returned by the OAuth2 provider
 */
@Entity
@Table(name = "app_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId(){return id;}

    /** Unique username or email address */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * BCrypt-hashed password.
     * NULL for OAuth2-only users (they never set a local password).
     */
    private String password;

    /**
     * Spring Security role string, e.g. "ROLE_USER" or "ROLE_ADMIN".
     * Stored without the ROLE_ prefix here and added back at runtime.
     */
    @Column(nullable = false)
    private String role;

    public String getUsername(){return username;}
    public String getPassword(){return password;}
    public String getRole(){return role;}

    /**
     * Auth provider: "LOCAL" for username/password login,
     * "GOOGLE" or "GITHUB" for social login.
     */
    @Column(nullable = false)
    private String provider;

    /** The ID the OAuth2 provider uses to identify this user (null for LOCAL). */
    private String providerId;
}
