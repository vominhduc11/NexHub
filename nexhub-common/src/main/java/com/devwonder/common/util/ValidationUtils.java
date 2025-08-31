package com.devwonder.common.util;

import com.devwonder.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[+]?[0-9\\s\\-()]{10,15}$");

    private ValidationUtils() {
        // Utility class
    }

    /**
     * Validate that a string is not null or empty
     */
    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be null or empty");
        }
    }

    /**
     * Validate that an object is not null
     */
    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }

    /**
     * Validate email format
     */
    public static void validateEmail(String email) {
        requireNonEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    /**
     * Validate phone format
     */
    public static void validatePhone(String phone) {
        requireNonEmpty(phone, "Phone");
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new ValidationException("Invalid phone format: " + phone);
        }
    }

    /**
     * Validate that an entity is not soft deleted
     */
    public static void requireNotDeleted(Object deletedAt, String entityName, Object id) {
        if (deletedAt != null) {
            throw new ValidationException(entityName + " with ID " + id + " has been deleted");
        }
    }

    /**
     * Generic validation with custom predicate
     */
    public static <T> void validate(T value, Predicate<T> predicate, String errorMessage) {
        if (!predicate.test(value)) {
            throw new ValidationException(errorMessage);
        }
    }

    /**
     * Validate string length
     */
    public static void validateLength(String value, String fieldName, int minLength, int maxLength) {
        requireNonEmpty(value, fieldName);
        int length = value.trim().length();
        if (length < minLength || length > maxLength) {
            throw new ValidationException(
                String.format("%s must be between %d and %d characters", fieldName, minLength, maxLength)
            );
        }
    }

    /**
     * Validate positive number
     */
    public static void validatePositive(Number value, String fieldName) {
        requireNonNull(value, fieldName);
        if (value.doubleValue() <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }

    /**
     * Validate non-negative number
     */
    public static void validateNonNegative(Number value, String fieldName) {
        requireNonNull(value, fieldName);
        if (value.doubleValue() < 0) {
            throw new ValidationException(fieldName + " cannot be negative");
        }
    }

    /**
     * Custom validation exception
     */
    public static class ValidationException extends BaseException {
        public ValidationException(String message) {
            super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        }
    }
}