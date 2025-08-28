package com.devwonder.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResellerRegistrationRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 6, max = 50, message = "Username must be between 6 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "Username must start with a letter and contain both letters and numbers")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    // Reseller profile information
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;
    
    @Pattern(regexp = "^[\\d\\-\\+\\(\\)\\s]+$", message = "Phone number format is invalid")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phone;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;
    
    @Size(max = 100, message = "District cannot exceed 100 characters")
    private String district;
    
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;
}