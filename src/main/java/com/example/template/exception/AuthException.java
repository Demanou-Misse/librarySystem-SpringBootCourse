package com.example.template.exception;

/**
 * Custom exception for authentication and authorization business logic.
 * The @ResponseStatus ensures that if not caught, it returns a 400 Bad Request.
 */
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }
}

