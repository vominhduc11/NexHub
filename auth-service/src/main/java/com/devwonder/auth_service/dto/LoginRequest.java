package com.devwonder.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "ADMIN|CUSTOMER|RESELLER", message = "User type must be ADMIN, CUSTOMER, or RESELLER")
    private String userType;
}