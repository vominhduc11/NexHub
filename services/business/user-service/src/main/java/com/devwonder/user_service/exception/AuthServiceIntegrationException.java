package com.devwonder.user_service.exception;

import com.devwonder.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AuthServiceIntegrationException extends BaseException {
    
    public AuthServiceIntegrationException(String message) {
        super("AUTH_SERVICE_INTEGRATION_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
    public AuthServiceIntegrationException(String message, Throwable cause) {
        super("AUTH_SERVICE_INTEGRATION_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause);
    }
}