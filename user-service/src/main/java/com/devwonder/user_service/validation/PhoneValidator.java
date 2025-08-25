package com.devwonder.user_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    
    private static final String PHONE_PATTERN = "^[\\d\\-\\+\\(\\)\\s]{10,15}$";
    
    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }
        
        // Remove all spaces, hyphens, parentheses, and plus signs for digit count
        String digitsOnly = phone.replaceAll("[\\s\\-\\(\\)\\+]", "");
        
        // Check if it contains only digits after cleanup
        if (!digitsOnly.matches("\\d+")) {
            return false;
        }
        
        // Check if digit count is between 10-15
        int digitCount = digitsOnly.length();
        return digitCount >= 10 && digitCount <= 15 && phone.matches(PHONE_PATTERN);
    }
}