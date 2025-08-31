package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {
    private static final String ERROR_CODE = "VALIDATION_ERROR";

    public ValidationException(String message) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String fieldName, String reason) {
        super(String.format("Validation failed for field '%s': %s", fieldName, reason), 
              ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST, cause);
    }
}