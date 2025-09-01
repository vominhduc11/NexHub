package com.devwonder.common.exception;

import com.devwonder.common.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all NexHub services.
 * This handler is shared across all microservices via nexhub-common library.
 * 
 * Uses @Order to allow service-specific handlers to override if needed.
 * No Swagger annotations to avoid interfering with API documentation.
 */
@RestControllerAdvice
@Slf4j
@Order(100) // Lower precedence - allows service-specific handlers to override
public class GlobalExceptionHandler {

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            "Validation failed", 
            "VALIDATION_ERROR", 
            validationErrors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle malformed JSON request body
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Invalid JSON format: {}", ex.getMessage());
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            "Invalid JSON format in request body", 
            "INVALID_JSON_FORMAT"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle type conversion errors (e.g., invalid path parameters)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Type mismatch error: {}", ex.getMessage());
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName()),
            "TYPE_MISMATCH_ERROR"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle custom business exceptions
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleBusinessException(BusinessException ex) {
        log.error("Business exception: {}", ex.getMessage());
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            ex.getMessage(),
            ex.getErrorCode()
        );
        
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }
    
    /**
     * Handle custom validation exceptions
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationException(ValidationException ex) {
        log.error("Custom validation exception: {}", ex.getMessage());
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            ex.getMessage(),
            ex.getErrorCode()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication exception: {}", ex.getMessage());
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            ex.getMessage(),
            ex.getErrorCode()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    /**
     * Handle authorization exceptions
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<BaseResponse<Object>> handleAuthorizationException(AuthorizationException ex) {
        log.error("Authorization exception: {}", ex.getMessage());
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            ex.getMessage(),
            ex.getErrorCode()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found exception: {}", ex.getMessage());
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            ex.getMessage(),
            ex.getErrorCode()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle configuration exceptions
     */
    @ExceptionHandler(ConfigurationException.class)
    public ResponseEntity<BaseResponse<Object>> handleConfigurationException(ConfigurationException ex) {
        log.error("Configuration exception: {}", ex.getMessage());
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            "Configuration error occurred",
            ex.getErrorCode()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            ex.getMessage(),
            "ILLEGAL_ARGUMENT"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle all other unexpected exceptions
     * This should be the last handler with lowest precedence
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        BaseResponse<Object> errorResponse = BaseResponse.error(
            "An unexpected error occurred. Please try again later.",
            "INTERNAL_SERVER_ERROR"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}