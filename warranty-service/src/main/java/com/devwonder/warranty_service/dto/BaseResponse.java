package com.devwonder.warranty_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(true, message, data, null);
    }
    
    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<>(true, message, null, null);
    }
    
    public static <T> BaseResponse<T> error(String message, String errorCode) {
        return new BaseResponse<>(false, message, null, errorCode);
    }
}