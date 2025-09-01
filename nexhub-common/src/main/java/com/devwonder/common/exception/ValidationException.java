package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST.value());
    }
    
    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", 
              String.format("Validation failed for field '%s': %s", field, message), 
              HttpStatus.BAD_REQUEST.value());
    }
}