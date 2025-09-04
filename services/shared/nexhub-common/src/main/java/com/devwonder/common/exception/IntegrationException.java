package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class IntegrationException extends BaseException {
    
    private static final String INTEGRATION_ERROR_MESSAGE_FORMAT = "Integration error with %s: %s";
    
    public IntegrationException(String service, String message) {
        super("SERVICE_INTEGRATION_ERROR", 
              String.format(INTEGRATION_ERROR_MESSAGE_FORMAT, service, message), 
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
    public IntegrationException(String service, String message, Throwable cause) {
        super("SERVICE_INTEGRATION_ERROR", 
              String.format(INTEGRATION_ERROR_MESSAGE_FORMAT, service, message), 
              HttpStatus.INTERNAL_SERVER_ERROR.value(), cause);
    }
    
    public IntegrationException(String errorCode, String service, String message) {
        super(errorCode, 
              String.format(INTEGRATION_ERROR_MESSAGE_FORMAT, service, message), 
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
