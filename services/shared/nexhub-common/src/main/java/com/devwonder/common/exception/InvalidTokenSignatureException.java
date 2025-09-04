package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenSignatureException extends BaseException {
    
    public InvalidTokenSignatureException(String message) {
        super("INVALID_TOKEN_SIGNATURE", message, HttpStatus.UNAUTHORIZED.value());
    }
    
    public InvalidTokenSignatureException(String message, Throwable cause) {
        super("INVALID_TOKEN_SIGNATURE", message, HttpStatus.UNAUTHORIZED.value(), cause);
    }
}