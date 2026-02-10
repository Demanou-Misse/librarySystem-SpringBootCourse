package com.example.template.auth.dto;

import java.util.List;

/**
 * Data Transfer Object returned upon successful authentication.
 * Includes the JWT and basic user identity information.
 */
public record AuthResponseDTO(
        String token,
        String email,
        List<String> roles
) {}


