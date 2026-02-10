package com.example.template.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordUpdateDTO(
        @Schema(description = "The current password for verification", example = "oldPassword123")
        @NotBlank String oldPassword,

        @Schema(description = "The new password to set", example = "newSecurePass2026")
        @NotBlank @Size(min = 8) String newPassword
) {}
