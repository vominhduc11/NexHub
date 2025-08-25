package com.devwonder.product_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UrlValidator implements ConstraintValidator<ValidImageUrl, String> {
    
    private static final String IMAGE_URL_PATTERN = 
        "^https?://.*\\.(jpg|jpeg|png|gif|webp)$";
    
    @Override
    public void initialize(ValidImageUrl constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url == null || url.trim().isEmpty()) {
            return true; // Let other annotations handle null/empty validation
        }
        
        return url.matches(IMAGE_URL_PATTERN);
    }
}