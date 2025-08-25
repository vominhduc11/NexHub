package com.devwonder.product_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = UrlValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageUrl {
    
    String message() default "Image URL must be a valid HTTP/HTTPS URL ending with jpg, jpeg, png, gif, or webp";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}