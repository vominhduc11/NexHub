package com.devwonder.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private ApiError error;
    private LocalDateTime timestamp;

    // Success response constructors
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(true, message, data, null, LocalDateTime.now());
    }

    public static <T> BaseResponse<T> success(T data) {
        return success(data, "Operation completed successfully");
    }

    public static BaseResponse<Void> success(String message) {
        return new BaseResponse<>(true, message, null, null, LocalDateTime.now());
    }

    public static BaseResponse<Void> success() {
        return success("Operation completed successfully");
    }

    // Error response constructors
    public static <T> BaseResponse<T> error(String message, ApiError error) {
        return new BaseResponse<>(false, message, null, error, LocalDateTime.now());
    }

    public static <T> BaseResponse<T> error(String message, String errorCode) {
        ApiError apiError = new ApiError(errorCode, null);
        return error(message, apiError);
    }

    public static <T> BaseResponse<T> error(String message, String errorCode, Object details) {
        ApiError apiError = new ApiError(errorCode, details);
        return error(message, apiError);
    }

    // Additional constructor for backward compatibility with simple error codes
    public static <T> BaseResponse<T> errorWithCode(String message, String errorCode) {
        ApiError apiError = new ApiError(errorCode, null);
        return error(message, apiError);
    }
}