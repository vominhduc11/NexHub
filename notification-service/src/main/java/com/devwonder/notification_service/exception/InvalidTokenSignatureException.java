package com.devwonder.notification_service.exception;

public class InvalidTokenSignatureException extends JwtValidationException {
    public InvalidTokenSignatureException(String message) {
        super(message);
    }
}