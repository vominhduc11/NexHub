package com.devwonder.product_service.exception;

import com.devwonder.common.exception.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(Long categoryId) {
        super("Category", "id", String.valueOf(String.valueOf(categoryId)));
    }

    public CategoryNotFoundException(String fieldName, Object fieldValue) {
        super("Category", fieldName, String.valueOf(fieldValue));
    }
}