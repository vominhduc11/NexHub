package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class JwtValidationException extends BaseException {
    
    public JwtValidationException(String message) {
        super("JWT_VALIDATION_ERROR", message, HttpStatus.UNAUTHORIZED.value());
    }
    
    public JwtValidationException(String message, Throwable cause) {
        super("JWT_VALIDATION_ERROR", message, HttpStatus.UNAUTHORIZED.value(), cause);
    }
    
    public JwtValidationException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.UNAUTHORIZED.value());
    }
    
    public JwtValidationException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.UNAUTHORIZED.value(), cause);
    }
}