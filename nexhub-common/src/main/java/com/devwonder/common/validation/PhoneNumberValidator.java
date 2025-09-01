package com.devwonder.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    
    private static final Pattern VIETNAM_PHONE_PATTERN = 
        Pattern.compile("^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$");
    
    private static final Pattern INTERNATIONAL_PHONE_PATTERN = 
        Pattern.compile("^\\+[1-9]\\d{1,14}$");
    
    private String countryCode;
    
    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        this.countryCode = constraintAnnotation.countryCode();
    }
    
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        
        String cleanPhone = phoneNumber.replaceAll("\\s+", "");
        
        return switch (countryCode.toUpperCase()) {
            case "VN" -> VIETNAM_PHONE_PATTERN.matcher(cleanPhone).matches();
            case "INTERNATIONAL" -> INTERNATIONAL_PHONE_PATTERN.matcher(cleanPhone).matches();
            default -> VIETNAM_PHONE_PATTERN.matcher(cleanPhone).matches();
        };
    }
}