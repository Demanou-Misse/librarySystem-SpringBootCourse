package com.example.template.auth.controller;

import com.example.template.auth.dto.LoginAuthRequestDTO;
import com.example.template.auth.dto.RegisterAuthRequestDTO;
import com.example.template.auth.dto.AuthResponseDTO;
import com.example.template.auth.service.AuthService;
import com.example.template.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication management.
 * Standardized in 2026 to use the Unified Response Pattern (ApiResponse).
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth") // Added v1 for consistency with other controllers
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new member in the system.
     * Wraps the result in ApiResponse for consistency.
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns a JWT")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@RequestBody @Valid RegisterAuthRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        log.info("New user registered successfully: {}", request.email());

        // Wrapping AuthResponseDTO inside ApiResponse with 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    /**
     * Authenticates credentials and issues a JWT.
     * Uses 200 OK with a unified response wrapper.
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Validates credentials and issues a JWT")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody @Valid LoginAuthRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        log.info("User logged in successfully: {}", request.email());

        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }
}

