package com.example.template.exception;

import com.example.template.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;


/**
 * Global Exception Handler following 2026 Enterprise standards.
 * Centralizes all error responses into a unified JSON format.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleAlreadyExists(AlreadyExistsException ex) {
        log.warn("Conflict detected: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Access Denied: You do not have permission to access this resource.");
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuth(AuthException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials provided");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();

        String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();

        log.warn("Validation failed: {}", errorMessage);

        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxSizeException(Exception ex) {
        return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "The file is too large (Max 5MB)");
    }

    /**
     * Fallback for all other Exceptions and Errors.
     * Uses the static error helper from ApiResponse for consistency.
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponse<Object>> handleAll(Throwable ex) {
        log.error("CRITICAL ERROR DETECTED: ", ex);

        // In 2026, we use the static error() helper to maintain a unified JSON structure
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Server Error: " + ex.getMessage()));
    }

    /**
     * Helper method to build the final ResponseEntity using the ApiResponse record.
     */
    private ResponseEntity<ApiResponse<Object>> buildResponse(HttpStatus status, String message) {
        // Refactored to use the static error() method for cleaner code
        return new ResponseEntity<>(ApiResponse.error(message), status);
    }
}
