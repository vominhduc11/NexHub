package com.devwonder.user_service.exception;

import com.devwonder.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BaseException {
    public EmailAlreadyExistsException(String message) {
        super(message, "EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}