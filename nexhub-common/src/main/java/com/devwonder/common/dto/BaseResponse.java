package com.devwonder.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;
    
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, "Success", data, null, LocalDateTime.now());
    }
    
    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(true, message, data, null, LocalDateTime.now());
    }
    
    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<>(true, message, null, null, LocalDateTime.now());
    }
    
    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(false, message, null, null, LocalDateTime.now());
    }
    
    public static <T> BaseResponse<T> error(String errorCode, String message) {
        return new BaseResponse<>(false, message, null, errorCode, LocalDateTime.now());
    }
    
    public static <T> BaseResponse<T> error(String errorCode, String message, T data) {
        return new BaseResponse<>(false, message, data, errorCode, LocalDateTime.now());
    }
}