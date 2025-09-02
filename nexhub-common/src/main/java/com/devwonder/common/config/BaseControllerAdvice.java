package com.devwonder.common.config;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.exception.BusinessException;
import com.devwonder.common.exception.ResourceNotFoundException;
import com.devwonder.common.exception.ValidationException;
import com.devwonder.common.exception.TokenExpiredException;
import com.devwonder.common.exception.JwtValidationException;
import com.devwonder.common.exception.InvalidTokenSignatureException;
import com.devwonder.common.util.ResponseUtil;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "com.devwonder")
@Hidden
public class BaseControllerAdvice {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Object>> handleBaseException(BaseException e) {
        log.error("BaseException occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(e.getMessage(), e.getErrorCode(), HttpStatus.valueOf(e.getHttpStatus()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationException(ValidationException e) {
        log.error("ValidationException occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(e.getMessage(), e.getErrorCode(), HttpStatus.valueOf(e.getHttpStatus()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleBusinessException(BusinessException e) {
        log.error("BusinessException occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(e.getMessage(), e.getErrorCode(), HttpStatus.valueOf(e.getHttpStatus()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.error("ResourceNotFoundException occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(e.getMessage(), e.getErrorCode(), HttpStatus.valueOf(e.getHttpStatus()));
    }

    @ExceptionHandler({TokenExpiredException.class, JwtValidationException.class, 
                      InvalidTokenSignatureException.class})
    public ResponseEntity<BaseResponse<Object>> handleJwtException(RuntimeException e) {
        log.error("JWT Exception occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(e.getMessage(), "JWT_VALIDATION_ERROR", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationErrors(MethodArgumentNotValidException e) {
        log.error("Validation failed: {}", e.getMessage(), e);
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String message = "Validation failed for fields: " + String.join(", ", errors.keySet());
        return ResponseUtil.error(message, "VALIDATION_FAILED", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<Object>> handleConstraintViolation(ConstraintViolationException e) {
        log.error("Constraint violation: {}", e.getMessage(), e);
        
        String violations = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        return ResponseUtil.error("Validation constraints violated: " + violations, 
                                "CONSTRAINT_VIOLATION", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("Invalid JSON request: {}", e.getMessage(), e);
        return ResponseUtil.error("Invalid request format", "INVALID_JSON", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.error("Type mismatch for parameter {}: {}", e.getName(), e.getMessage(), e);
        String requiredType = Optional.ofNullable(e.getRequiredType())
                                     .map(Class::getSimpleName)
                                     .orElse("unknown");
        String message = String.format("Invalid value for parameter '%s': expected %s", 
                                      e.getName(), requiredType);
        return ResponseUtil.error(message, "TYPE_MISMATCH", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Data integrity violation: {}", e.getMessage(), e);
        
        String message = "Data integrity constraint violated";
        if (e.getMessage().contains("Duplicate entry")) {
            message = "Duplicate entry - record already exists";
        } else if (e.getMessage().contains("foreign key constraint")) {
            message = "Cannot delete/update - record is referenced by other data";
        }
        
        return ResponseUtil.error(message, "DATA_INTEGRITY_VIOLATION", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse<Object>> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication failed: {}", e.getMessage(), e);
        return ResponseUtil.error("Authentication failed", "AUTHENTICATION_FAILED", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access denied: {}", e.getMessage(), e);
        return ResponseUtil.error("Access denied", "ACCESS_DENIED", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Invalid argument: {}", e.getMessage(), e);
        return ResponseUtil.error(e.getMessage(), "INVALID_ARGUMENT", HttpStatus.BAD_REQUEST);
    }


    protected ResponseEntity<BaseResponse<Object>> handleCustomException(String message, String errorCode, HttpStatus status) {
        return ResponseUtil.error(message, errorCode, status);
    }

    protected ResponseEntity<BaseResponse<Object>> handleCustomExceptionWithData(String message, String errorCode, HttpStatus status, Object data) {
        // Log the data for debugging purposes
        log.debug("Additional error data: {}", data);
        return ResponseUtil.error(message, errorCode, status);
    }
}