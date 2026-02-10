package com.example.template.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

/**
 * DTO for updating a Member's non-password fields.
 * Fields are optional for partial updates.
 */
public record MemberRequestUpdateDTO(
        @Schema(description = "Updated name of the member", example = "John Updated")
        String name,

        @Schema(description = "Updated email address", example = "john.updated@example.com")
        @Email(message = "Incorrect format")
        String email
) {}

