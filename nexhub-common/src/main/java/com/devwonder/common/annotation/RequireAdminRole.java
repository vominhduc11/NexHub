package com.devwonder.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to require ADMIN role for method execution.
 * When applied to a method, it will automatically check if the current user has ADMIN role
 * and return a 403 Forbidden response if not authorized.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAdminRole {
    
    /**
     * Custom error message to return when access is denied
     * @return the error message
     */
    String message() default "Access denied - ADMIN role required";
    
    /**
     * Custom error code to return when access is denied
     * @return the error code
     */
    String errorCode() default "ACCESS_DENIED";
}