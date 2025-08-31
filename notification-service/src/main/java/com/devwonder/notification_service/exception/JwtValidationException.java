package com.devwonder.notification_service.exception;

public class JwtValidationException extends Exception {
    public JwtValidationException(String message) {
        super(message);
    }

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}