package com.example.template.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of JwtService using JJWT library.
 * Standard 2026 implementation for stateless authentication.
 */
@Service
public class JwtServiceImpl implements JwtService {

    private final String secretKey;
    private final long jwtExpiration;

    public JwtServiceImpl(
            @Value("${application.security.jwt.secret-key}") String secretKey,
            @Value("${application.security.jwt.expiration}") long jwtExpiration
    ) {
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    /**
     * Extracts the subject (email) from the token.
     */
    @Override
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generic method to extract any claim from the token.
     */
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a token with only the subject.
     */
    @Override
    public String generateToken(String email) {
        return generateToken(new HashMap<>(), email);
    }

    /**
     * Generates a token with extra claims (like roles) and the subject.
     */
    @Override
    public String generateToken(Map<String, Object> extraClaims, String email) {
        return Jwts.builder()
                .claims(extraClaims) // Set custom claims (e.g. roles)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey()) // Sign with SHA-256 or higher
                .compact();
    }

    /**
     * Checks if the token is cryptographically valid and not expired.
     */
    @Override
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token); // The try/catch block ensures that malformed or tampered tokens result in a 'false' validation instead of crashing the security filter.
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // new Date() captures the current system time to validate the token's "exp" (expiration) claim.
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parses the JWT and returns all claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Decodes the Base64 secret key and creates a HMAC SHA Key.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
