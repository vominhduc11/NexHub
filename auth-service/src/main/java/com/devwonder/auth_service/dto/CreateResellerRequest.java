package com.devwonder.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateResellerRequest {
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String address;
    
    private String phone;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String district;
    
    private String city;
}