package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends BaseException {
    
    public BusinessException(String message) {
        super("BUSINESS_ERROR", message, HttpStatus.BAD_REQUEST.value());
    }
    
    public BusinessException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST.value());
    }
    
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.BAD_REQUEST.value(), cause);
    }
}