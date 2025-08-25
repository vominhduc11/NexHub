package com.devwonder.blog_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SlugValidator implements ConstraintValidator<ValidSlug, String> {
    
    private static final String SLUG_PATTERN = "^[a-z0-9-]+$";
    
    @Override
    public void initialize(ValidSlug constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String slug, ConstraintValidatorContext context) {
        if (slug == null || slug.trim().isEmpty()) {
            return false; // Slug is required
        }
        
        // Check if slug matches pattern
        if (!slug.matches(SLUG_PATTERN)) {
            return false;
        }
        
        // Check if slug doesn't start or end with hyphen
        if (slug.startsWith("-") || slug.endsWith("-")) {
            return false;
        }
        
        // Check for consecutive hyphens
        return !slug.contains("--");
    }
}