package com.devwonder.auth_service.exception;

import com.devwonder.common.exception.ConfigurationException;

public class TokenGenerationException extends ConfigurationException {
    public TokenGenerationException(String message) {
        super("Token generation failed: " + message);
    }

    public TokenGenerationException(String message, Throwable cause) {
        super("TOKEN_GENERATION_ERROR", "Token generation failed: " + message, cause);
    }
}