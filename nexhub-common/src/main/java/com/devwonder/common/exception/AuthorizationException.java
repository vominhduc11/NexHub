package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends BaseException {
    private static final String ERROR_CODE = "AUTHORIZATION_FAILED";

    public AuthorizationException(String message) {
        super(message, ERROR_CODE, HttpStatus.FORBIDDEN);
    }

    public AuthorizationException(String action, String resource) {
        super(String.format("Access denied: insufficient permissions to %s %s", action, resource), 
              ERROR_CODE, HttpStatus.FORBIDDEN);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.FORBIDDEN, cause);
    }
}