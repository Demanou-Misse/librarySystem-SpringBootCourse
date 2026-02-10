package com.example.template.dto.member;

/**
 * DTO for Member representation returned to the client.
 * Uses Java Record (Standard Pro 2026).
 */
public record MemberResponseDTO(
        Long id,
        String name,
        String email
) {}

