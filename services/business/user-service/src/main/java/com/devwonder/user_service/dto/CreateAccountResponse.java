package com.devwonder.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountResponse {
    
    private Long accountId;
    private String username;
    private String status;
    private String message;
}