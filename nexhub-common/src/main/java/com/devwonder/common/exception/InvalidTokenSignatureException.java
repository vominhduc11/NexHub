package com.devwonder.common.exception;

public class InvalidTokenSignatureException extends JwtValidationException {
    
    public InvalidTokenSignatureException(String message) {
        super(message);
    }
    
    public InvalidTokenSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}