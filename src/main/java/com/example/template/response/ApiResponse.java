package com.example.template.response;

import java.time.Instant;

/**
 * Modern API Response wrapper for 2026.
 * Uses Java Record for immutability and follows the "Unified Response" pattern.
 *
 * @param <T> The type of the data payload
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String timestamp
) {
    /**
     * Standard constructor for successful responses or errors with data.
     * Automatically adds the current timestamp.
     */
    public ApiResponse(boolean success, String message, T data) {
        this(success, message, data, Instant.now().toString());
    }

    /**
     * Static helper for success responses (standard in 2026 for clean service code).
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Static helper for error responses.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}

