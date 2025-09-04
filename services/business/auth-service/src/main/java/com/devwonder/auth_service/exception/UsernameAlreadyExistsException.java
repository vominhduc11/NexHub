package com.devwonder.auth_service.exception;

import com.devwonder.common.exception.BusinessException;

public class UsernameAlreadyExistsException extends BusinessException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}