package com.example.template.security;

import com.example.template.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom Entry Point - 2026 Unified Response Standard.
 * Handles unauthorized access attempts by returning a consistent ApiResponse JSON.
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // Log security rejection for auditing purposes
        log.warn("Unauthorized access attempt to {}: {}", request.getServletPath(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Standardizing the response using the unified ApiResponse record
        ApiResponse<Object> apiResponse = ApiResponse.error(
                "Authentication Required: " + authException.getMessage()
        );

        // Manually writing the JSON to the response stream
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}


