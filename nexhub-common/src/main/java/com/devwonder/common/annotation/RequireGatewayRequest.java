package com.devwonder.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to require requests to come through the API Gateway.
 * When applied to a method, it will automatically check for the X-Gateway-Request header
 * and return a 403 Forbidden response if the request doesn't come from the gateway.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireGatewayRequest {
    
    /**
     * Custom error message to return when access is denied
     * @return the error message
     */
    String message() default "Access denied - Request must come through API Gateway";
    
    /**
     * Custom error code to return when access is denied
     * @return the error code
     */
    String errorCode() default "FORBIDDEN_ACCESS";
}