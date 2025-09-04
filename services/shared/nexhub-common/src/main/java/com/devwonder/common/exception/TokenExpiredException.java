package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
    
    public TokenExpiredException(String message) {
        super("TOKEN_EXPIRED", message, HttpStatus.UNAUTHORIZED.value());
    }
    
    public TokenExpiredException(String message, Throwable cause) {
        super("TOKEN_EXPIRED", message, HttpStatus.UNAUTHORIZED.value(), cause);
    }
}