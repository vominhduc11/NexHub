package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class JwksRetrievalException extends BaseException {
    
    public JwksRetrievalException(String message) {
        super("JWKS_RETRIEVAL_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
    public JwksRetrievalException(String message, Throwable cause) {
        super("JWKS_RETRIEVAL_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause);
    }
}