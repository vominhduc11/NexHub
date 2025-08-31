package com.devwonder.auth_service.exception;

import com.devwonder.common.exception.BusinessException;

public class UserAlreadyExistsException extends BusinessException {
    public UserAlreadyExistsException(String username) {
        super(String.format("User with username '%s' already exists", username));
    }

    public UserAlreadyExistsException(String fieldName, String value) {
        super(String.format("User with %s '%s' already exists", fieldName, value));
    }
}