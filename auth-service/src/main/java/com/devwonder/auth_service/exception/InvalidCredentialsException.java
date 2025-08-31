package com.devwonder.auth_service.exception;

import com.devwonder.common.exception.AuthenticationException;

public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException() {
        super("Invalid username or password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}