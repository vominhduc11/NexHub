package com.devwonder.auth_service.exception;

import com.devwonder.common.exception.BusinessException;

public class UserServiceIntegrationException extends BusinessException {
    public UserServiceIntegrationException(String message) {
        super("User service integration failed: " + message);
    }

    public UserServiceIntegrationException(String message, Throwable cause) {
        super("USER_SERVICE_INTEGRATION_ERROR", "User service integration failed: " + message, cause);
    }
}