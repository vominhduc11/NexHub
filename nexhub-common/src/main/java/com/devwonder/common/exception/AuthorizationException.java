package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends BaseException {
    
    public AuthorizationException(String message) {
        super("AUTHORIZATION_ERROR", message, HttpStatus.FORBIDDEN.value());
    }
    
    public AuthorizationException(String message, String errorCode) {
        super(errorCode, message, HttpStatus.FORBIDDEN.value());
    }
    
    public AuthorizationException(String message, Throwable cause) {
        super("AUTHORIZATION_ERROR", message, HttpStatus.FORBIDDEN.value(), cause);
    }
    
    public AuthorizationException(String message, String errorCode, Throwable cause) {
        super(errorCode, message, HttpStatus.FORBIDDEN.value(), cause);
    }
}