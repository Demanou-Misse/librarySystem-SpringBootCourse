package com.example.template.security;

import com.example.template.jwt.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Enterprise-grade Unit Tests for JwtServiceImpl.
 * Standards: JUnit 5, AssertJ, Constructor Injection, and Type-safe Claims.
 */
@DisplayName("Security - JWT Service Specifications")
class JwtServiceTest {

    private JwtServiceImpl jwtService;
    private final String testEmail = "admin@example.com";

    // 2026 Security: Base64 encoded secret meeting the 256-bit requirement for HS256
    private final String secureSecret = Base64.getEncoder()
            .encodeToString("a-very-strong-and-secret-key-32-chars-minimum-2026-standard".getBytes());
    private final long expirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        // Professional approach: Injecting dependencies via constructor
        // to avoid ReflectionTestUtils and ensure better test speed.
        jwtService = new JwtServiceImpl(secureSecret, expirationMs);
    }

    @Nested
    @DisplayName("Token Generation & Claims Extraction")
    class ExtractionTests {

        @Test
        @DisplayName("Should correctly generate token and extract custom roles")
        @SuppressWarnings("unchecked")
        void shouldGenerateAndExtractRoles() {
            // Arrange
            List<String> roles = List.of("ROLE_ADMIN", "ROLE_USER");
            Map<String, Object> extraClaims = Map.of("roles", roles);

            // Act
            String token = jwtService.generateToken(extraClaims, testEmail);

            // Assert: Subject verification
            assertThat(jwtService.extractSubject(token)).isEqualTo(testEmail);

            // Assert: Role verification with explicit casting to solve "Capture of ?" error
            List<String> extractedRoles = (List<String>) jwtService.extractClaim(token,
                    claims -> claims.get("roles", List.class));

            assertThat(extractedRoles)
                    .isNotNull()
                    .hasSize(2)
                    .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        }
    }

    @Nested
    @DisplayName("Token Validity & Security Integrity")
    class ValidityTests {

        @Test
        @DisplayName("Should return true for a legitimate and active token")
        void shouldValidateLegitToken() {
            String token = jwtService.generateToken(testEmail);

            // Matches your implementation: isTokenValid(String token)
            assertThat(jwtService.isTokenValid(token)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "not.a.valid.token",
                "header.payload.tamperedsignature",
                "",
                "   "
        })
        @DisplayName("Should return false for malformed or tampered tokens")
        void shouldRejectInvalidTokens(String candidate) {
            assertThat(jwtService.isTokenValid(candidate)).isFalse();
        }

        @Test
        @DisplayName("Should invalidate token once it passes its expiration date")
        void shouldInvalidateExpiredToken() {
            // Arrange: Create a service instance where tokens expire instantly (0ms)
            JwtServiceImpl expiredService = new JwtServiceImpl(secureSecret, 0);
            String token = expiredService.generateToken(testEmail);

            // Act & Assert
            // Since isTokenExpired is private, we test through the public isTokenValid method
            assertThat(expiredService.isTokenValid(token)).isFalse();
        }
    }
}
