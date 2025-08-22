package com.devwonder.auth_service.exception;

import com.devwonder.auth_service.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.devwonder.auth_service.controller")
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleUsernameAlreadyExists(UsernameAlreadyExistsException e) {
        log.error("Username already exists: {}", e.getMessage());
        BaseResponse<Void> response = BaseResponse.error(e.getMessage(), "USERNAME_EXISTS");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleRoleNotFound(RoleNotFoundException e) {
        log.error("Role not found: {}", e.getMessage());
        BaseResponse<Void> response = BaseResponse.error(e.getMessage(), "ROLE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());
        Map<String, String> fieldErrors = new HashMap<>();
        
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        BaseResponse<Void> response = BaseResponse.error("Invalid input data", "VALIDATION_ERROR", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime error: {}", e.getMessage(), e);
        
        if (e.getMessage().contains("Failed to create reseller profile")) {
            BaseResponse<Void> response = BaseResponse.error("Failed to complete registration", "REGISTRATION_FAILED");
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