package com.devwonder.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResellerRegistrationResponse {
    private Long id;
    private String username;
    private String message;
}