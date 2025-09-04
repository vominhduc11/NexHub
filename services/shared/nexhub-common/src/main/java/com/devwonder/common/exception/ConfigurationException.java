package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class ConfigurationException extends BaseException {
    
    public ConfigurationException(String message) {
        super("CONFIGURATION_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
    public ConfigurationException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
    public ConfigurationException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause);
    }
}