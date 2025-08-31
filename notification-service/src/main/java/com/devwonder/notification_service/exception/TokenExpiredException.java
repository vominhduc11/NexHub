package com.devwonder.notification_service.exception;

public class TokenExpiredException extends JwtValidationException {
    public TokenExpiredException(String message) {
        super(message);
    }
}