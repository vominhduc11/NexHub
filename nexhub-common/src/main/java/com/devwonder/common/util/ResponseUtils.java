package com.devwonder.common.util;

import com.devwonder.common.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseUtils {
    
    private ResponseUtils() {
        // Utility class
    }

    /**
     * Create successful response with data
     */
    public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
        return ResponseEntity.ok(BaseResponse.success(data));
    }

    /**
     * Create successful response with data and message
     */
    public static <T> ResponseEntity<BaseResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(BaseResponse.success(data, message));
    }

    /**
     * Create created response with data
     */
    public static <T> ResponseEntity<BaseResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(data, "Resource created successfully"));
    }

    /**
     * Create created response with data and message
     */
    public static <T> ResponseEntity<BaseResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(data, message));
    }

    /**
     * Create no content response
     */
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Create error response with message and error code
     */
    public static <T> ResponseEntity<BaseResponse<T>> error(String message, String errorCode, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(BaseResponse.error(message, errorCode));
    }

    /**
     * Create bad request response
     */
    public static <T> ResponseEntity<BaseResponse<T>> badRequest(String message, String errorCode) {
        return error(message, errorCode, HttpStatus.BAD_REQUEST);
    }

    /**
     * Create not found response
     */
    public static <T> ResponseEntity<BaseResponse<T>> notFound(String message, String errorCode) {
        return error(message, errorCode, HttpStatus.NOT_FOUND);
    }

    /**
     * Create internal server error response
     */
    public static <T> ResponseEntity<BaseResponse<T>> internalError() {
        return error("Internal server error", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Create internal server error response with custom message
     */
    public static <T> ResponseEntity<BaseResponse<T>> internalError(String message) {
        return error(message, "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Create conflict response
     */
    public static <T> ResponseEntity<BaseResponse<T>> conflict(String message, String errorCode) {
        return error(message, errorCode, HttpStatus.CONFLICT);
    }

    /**
     * Create unauthorized response
     */
    public static <T> ResponseEntity<BaseResponse<T>> unauthorized(String message) {
        return error(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Create forbidden response
     */
    public static <T> ResponseEntity<BaseResponse<T>> forbidden(String message) {
        return error(message, "FORBIDDEN", HttpStatus.FORBIDDEN);
    }
}