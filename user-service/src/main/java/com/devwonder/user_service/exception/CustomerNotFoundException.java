package com.devwonder.user_service.exception;

import com.devwonder.common.exception.ResourceNotFoundException;

public class CustomerNotFoundException extends ResourceNotFoundException {
    public CustomerNotFoundException(Long customerId) {
        super("Customer", "id", customerId);
    }

    public CustomerNotFoundException(String fieldName, Object fieldValue) {
        super("Customer", fieldName, fieldValue);
    }
}