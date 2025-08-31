package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    private static final String ERROR_CODE = "AUTHENTICATION_FAILED";

    public AuthenticationException(String message) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED, cause);
    }
}