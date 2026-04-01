package com.app.quantitymeasurement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService – the heart of JWT-based authentication.
 *
 * ┌─────────────────────────────────────────────────────┐
 * │  What is a JWT?                                     │
 * │                                                     │
 * │  A JWT (JSON Web Token) is a compact, self-contained│
 * │  string with 3 base64-encoded parts separated by   │
 * │  dots:                                              │
 * │    HEADER.PAYLOAD.SIGNATURE                         │
 * │                                                     │
 * │  Header  – algorithm used (HS256)                   │
 * │  Payload – "claims": sub (username), role,          │
 * │            iat (issued-at), exp (expires)           │
 * │  Signature – HMAC-SHA256 of header+payload using    │
 * │              our secret key                         │
 * │                                                     │
 * │  The server verifies the signature on every request.│
 * │  No session/cookie is needed – it is stateless.     │
 * └─────────────────────────────────────────────────────┘
 *
 * Configuration (application.properties):
 *   jwt.secret      – Base64-encoded 256-bit HMAC key
 *   jwt.expiration  – Token lifetime in milliseconds (default 24 h)
 */
@Service
public class JwtService {

    /** Base64-encoded secret key injected from application.properties */
    @Value("${jwt.secret}")
    private String secretKey;

    /** Token expiration in milliseconds (default: 86 400 000 ms = 24 hours) */
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    // ─────────────────────────── Token Generation ───────────────────────────

    /**
     * Generate a JWT token for the given user.
     * Adds a "role" claim so the filter can assign authorities without a DB call.
     *
     * @param userDetails the authenticated user
     * @return signed JWT string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        // Store the role so we can read it from the token on future requests
        extraClaims.put("role", userDetails.getAuthorities()
                .stream().findFirst()
                .map(Object::toString).orElse("ROLE_USER"));
        return buildToken(extraClaims, userDetails);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)                         // custom claims (role)
                .setSubject(userDetails.getUsername())          // "sub" claim = username
                .setIssuedAt(new Date(System.currentTimeMillis()))  // "iat"
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // "exp"
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // sign with secret
                .compact();                                     // serialize to String
    }

    // ─────────────────────────── Token Validation ───────────────────────────

    /**
     * Validate a token: checks signature, expiry, and that the username matches.
     *
     * @param token       the JWT from the Authorization header
     * @param userDetails the user loaded from DB
     * @return true if valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ─────────────────────────── Claims Extraction ──────────────────────────

    /** Extract the username ("sub" claim) from the token */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Extract the role custom claim */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /** Extract expiration as a human-readable string for AuthResponse */
    public String extractExpirationString(String token) {
        Date exp = extractExpiration(token);
        return DateTimeFormatter.ISO_DATE_TIME
                .withZone(ZoneId.of("UTC"))
                .format(exp.toInstant());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parse and verify the token, throwing JwtException if invalid/expired.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new JwtException("Invalid or expired JWT token: " + e.getMessage());
        }
    }

    /** Decode the base64 secret key into a Key object */
    private Key getSigningKey() {
        byte[] keyBytes;
        try {
            // Try URL-safe base64 first (handles "-" and "_" characters)
            keyBytes = Decoders.BASE64URL.decode(secretKey);
        } catch (Exception e) {
            // Fall back to standard base64
            keyBytes = Decoders.BASE64.decode(secretKey);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** Expose expiration millis so AuthResponse can compute the expiry time */
    public long getJwtExpiration() {
        return jwtExpiration;
    }
}
