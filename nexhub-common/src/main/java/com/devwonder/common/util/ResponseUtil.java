package com.devwonder.common.util;

import com.devwonder.common.dto.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public final class ResponseUtil {
    
    private ResponseUtil() {}
    
    public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
        return ResponseEntity.ok(BaseResponse.success(data));
    }
    
    public static <T> ResponseEntity<BaseResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(BaseResponse.success(message, data));
    }
    
    public static ResponseEntity<BaseResponse<String>> success(String message) {
        return ResponseEntity.ok(BaseResponse.success(message));
    }
    
    public static ResponseEntity<BaseResponse<Void>> successVoid(String message) {
        return ResponseEntity.ok(BaseResponse.success(message));
    }
    
    public static <T> ResponseEntity<BaseResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BaseResponse.success("Created successfully", data));
    }
    
    public static <T> ResponseEntity<BaseResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BaseResponse.success(message, data));
    }
    
    public static <T> ResponseEntity<BaseResponse<T>> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(BaseResponse.error("BAD_REQUEST", message));
    }
    
    public static <T> ResponseEntity<BaseResponse<T>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(BaseResponse.error("NOT_FOUND", message));
    }
    
    public static <T> ResponseEntity<BaseResponse<T>> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(BaseResponse.error("UNAUTHORIZED", message));
    }
    
    public static <T> ResponseEntity<BaseResponse<T>> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(BaseResponse.error("FORBIDDEN", message));
    }
    
    public static <T> ResponseEntity<BaseResponse<T>> internalError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(BaseResponse.error("INTERNAL_SERVER_ERROR", message));
    }
    
    public static <T> ResponseEntity<BaseResponse<T>> error(String message, String errorCode, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus)
            .body(BaseResponse.error(errorCode, message));
    }
    
    public static <T> ResponseEntity<BaseResponse<T>> error(String message, String errorCode, int httpStatus) {
        return error(message, errorCode, HttpStatus.valueOf(httpStatus));
    }
    
    public static <T> ResponseEntity<BaseResponse<Map<String, Object>>> paginatedSuccess(
            Page<T> page, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber());
        response.put("size", page.getSize());
        response.put("first", page.isFirst());
        response.put("last", page.isLast());
        
        return ResponseEntity.ok(BaseResponse.success(message, response));
    }
    
    public static <T> ResponseEntity<BaseResponse<Map<String, Object>>> paginatedSuccess(Page<T> page) {
        return paginatedSuccess(page, "Data retrieved successfully");
    }
}