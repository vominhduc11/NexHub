package com.devwonder.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String code;
    private Object details;
    
    public ApiError(String code) {
        this.code = code;
        this.details = null;
    }
}