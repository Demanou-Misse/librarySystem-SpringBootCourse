package com.example.template.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs are implemented as records because they are immutable,
 * concise, and designed to represent pure data structures
 * without business logic or state mutation.
 */
public record MemberRequestDTO(
        @Schema(description = "Name of the member", example = "John")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(description = "Email address", example = "john@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Incorrect format")
        String email,

        @Schema(description = "Secure password (min 8 characters)", example = "SecurePass123!")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {}

