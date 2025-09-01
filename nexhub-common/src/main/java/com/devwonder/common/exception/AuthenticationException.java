package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    
    public AuthenticationException(String message) {
        super("AUTHENTICATION_FAILED", message, HttpStatus.UNAUTHORIZED.value());
    }
    
    public AuthenticationException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.UNAUTHORIZED.value());
    }
    
    public AuthenticationException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.UNAUTHORIZED.value(), cause);
    }
}