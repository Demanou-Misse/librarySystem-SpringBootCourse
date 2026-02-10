package com.example.template.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for authentication.
 * Uses Java Record for immutability.
 */
public record RegisterAuthRequestDTO(
        @Schema(description = "Full name of the member", example = "John Doe")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(description = "Unique email address for registration", example = "john.doe@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "Secure password (min 8 characters)", example = "SecurePass123!")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}
