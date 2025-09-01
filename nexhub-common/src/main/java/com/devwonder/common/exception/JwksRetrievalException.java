package com.devwonder.common.exception;

public class JwksRetrievalException extends JwtValidationException {
    
    public JwksRetrievalException(String message) {
        super(message);
    }
    
    public JwksRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}