package com.devwonder.blog_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = SlugValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSlug {
    
    String message() default "Slug must contain only lowercase letters, numbers, and hyphens, cannot start/end with hyphens, and cannot have consecutive hyphens";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}