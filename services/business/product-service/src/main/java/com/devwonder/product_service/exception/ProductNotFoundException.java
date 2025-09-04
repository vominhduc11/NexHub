package com.devwonder.product_service.exception;

import com.devwonder.common.exception.ResourceNotFoundException;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException(Long productId) {
        super("Product", "id", String.valueOf(String.valueOf(productId)));
    }

    public ProductNotFoundException(String fieldName, Object fieldValue) {
        super("Product", fieldName, String.valueOf(fieldValue));
    }
}