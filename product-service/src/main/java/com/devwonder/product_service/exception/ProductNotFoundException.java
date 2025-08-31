package com.devwonder.product_service.exception;

import com.devwonder.common.exception.ResourceNotFoundException;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException(Long productId) {
        super("Product", "id", productId);
    }

    public ProductNotFoundException(String fieldName, Object fieldValue) {
        super("Product", fieldName, fieldValue);
    }
}