package com.devwonder.notification_service.exception;

public class JwksRetrievalException extends Exception {
    public JwksRetrievalException(String message) {
        super(message);
    }

    public JwksRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}