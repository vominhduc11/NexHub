package com.devwonder.user_service.exception;

import com.devwonder.common.exception.ResourceNotFoundException;

public class ResellerNotFoundException extends ResourceNotFoundException {
    public ResellerNotFoundException(Long resellerId) {
        super("Reseller", "id", String.valueOf(resellerId));
    }

    public ResellerNotFoundException(String fieldName, Object fieldValue) {
        super("Reseller", fieldName, String.valueOf(fieldValue));
    }

    public ResellerNotFoundException(String message) {
        super(message);
    }
}