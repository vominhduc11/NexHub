package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class ConfigurationException extends BaseException {
    private static final String ERROR_CODE = "CONFIGURATION_ERROR";

    public ConfigurationException(String message) {
        super(message, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ConfigurationException(String configKey, String reason) {
        super(String.format("Configuration error for '%s': %s", configKey, reason), 
              ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}