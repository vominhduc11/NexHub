package com.devwonder.warranty_service.exception;

import com.devwonder.common.exception.ResourceNotFoundException;

public class PurchasedProductNotFoundException extends ResourceNotFoundException {
    public PurchasedProductNotFoundException(Long productId) {
        super("Purchased product", "id", productId);
    }

    public PurchasedProductNotFoundException(String fieldName, Object fieldValue) {
        super("Purchased product", fieldName, fieldValue);
    }
}