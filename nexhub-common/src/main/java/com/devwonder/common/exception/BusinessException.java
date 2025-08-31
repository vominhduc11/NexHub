package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends BaseException {
    private static final String ERROR_CODE = "BUSINESS_RULE_VIOLATION";

    public BusinessException(String message) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST, cause);
    }

    public BusinessException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }
}