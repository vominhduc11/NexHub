package com.devwonder.user_service.exception;

import com.devwonder.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PhoneAlreadyExistsException extends BaseException {
    public PhoneAlreadyExistsException(String message) {
        super("PHONE_ALREADY_EXISTS", message, HttpStatus.CONFLICT.value());
    }
}