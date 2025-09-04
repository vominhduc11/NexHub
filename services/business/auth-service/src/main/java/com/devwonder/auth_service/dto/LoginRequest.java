package com.devwonder.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 3, max = 100, message = "Password must be between 3 and 100 characters")
    private String password;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "ADMIN|CUSTOMER|DEALER", message = "User type must be ADMIN, CUSTOMER, or DEALER")
    private String userType;
}