package com.devwonder.common.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ValidationUtil {
    
    private final Validator validator;
    
    public ValidationUtil(Validator validator) {
        this.validator = validator;
    }
    
    public <T> void validateAndThrow(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            throw new com.devwonder.common.exception.ValidationException(errorMessage);
        }
    }
    
    public <T> boolean isValid(T object) {
        return validator.validate(object).isEmpty();
    }
    
    public <T> Set<String> getValidationErrors(T object) {
        return validator.validate(object).stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());
    }
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
    
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }
    
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        // At least 8 characters, with at least one letter and one number
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$");
    }
    
    public static boolean isValidVietnamesePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        String cleanPhone = phoneNumber.replaceAll("\\s+", "");
        return cleanPhone.matches("^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$");
    }
}