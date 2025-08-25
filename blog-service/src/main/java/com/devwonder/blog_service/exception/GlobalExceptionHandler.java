package com.devwonder.blog_service.exception;

import com.devwonder.blog_service.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// @RestControllerAdvice(basePackages = "com.devwonder.blog_service.controller")
@Order(1)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());
        Map<String, String> fieldErrors = new HashMap<>();
        
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        String errorMessage = "Invalid input data: " + fieldErrors.values().stream()
            .collect(Collectors.joining(", "));
        
        BaseResponse<Map<String, String>> response = BaseResponse.error(errorMessage, "VALIDATION_ERROR");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument error: {}", e.getMessage());
        BaseResponse<Void> response = BaseResponse.error(e.getMessage(), "ILLEGAL_ARGUMENT");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime error: {}", e.getMessage(), e);
        
        if (e.getMessage().contains("not found")) {
            BaseResponse<Void> response = BaseResponse.error("Resource not found", "RESOURCE_NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        if (e.getMessage().contains("already exists")) {
            BaseResponse<Void> response = BaseResponse.error("Resource already exists", "RESOURCE_EXISTS");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        
        if (e.getMessage().contains("blog") || e.getMessage().contains("comment")) {
            BaseResponse<Void> response = BaseResponse.error("Blog operation failed", "BLOG_OPERATION_ERROR");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        BaseResponse<Void> response = BaseResponse.error(e.getMessage(), "RUNTIME_ERROR");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        BaseResponse<Void> response = BaseResponse.error("An unexpected error occurred", "INTERNAL_SERVER_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}