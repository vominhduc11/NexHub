package com.devwonder.blog_service.dto;

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
    private String errorCode;
    private LocalDateTime timestamp;

    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(true, message, data, null, LocalDateTime.now());
    }

    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<>(true, message, null, null, LocalDateTime.now());
    }

    public static <T> BaseResponse<T> error(String message, String errorCode) {
        return new BaseResponse<>(false, message, null, errorCode, LocalDateTime.now());
    }
}