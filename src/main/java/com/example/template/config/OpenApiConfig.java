package com.example.template.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration - Enterprise Standard 2026.
 * Configures Swagger UI with JWT Bearer Authentication support.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Library Management API",
                version = "1.0.0",
                description = "Documentation for the Library Project Professional API",
                contact = @Contact(name = "Misse Junior", email = "missedemanou@gmail.com", url = "https://github.com/Demanou-Misse")
        ),
        security = @SecurityRequirement(name = "bearerAuth") // Global security requirement
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter your JWT token in the format: Bearer <token>"
)
public class OpenApiConfig {
}

