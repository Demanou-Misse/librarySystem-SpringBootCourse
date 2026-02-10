package com.example.template.jwt;

import io.jsonwebtoken.Claims;
import java.util.Map;
import java.util.function.Function;

/**
 * Interface for JWT operations.
 * Defines the contract for token generation, extraction, and validation.
 */
public interface JwtService {

    String extractSubject(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(String email);

    String generateToken(Map<String, Object> extraClaims, String email);

    boolean isTokenValid(String token);
}

