package com.devwonder.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateResellerRequest {
    
    @NotNull(message = "Account ID is required")
    @Positive(message = "Account ID must be positive")
    private Long accountId;
    
    // Username and password managed by Auth Service - only profile data here
    @NotBlank(message = "Tên công ty không được để trống")
    private String name;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại không hợp lệ (phải bắt đầu bằng 0 và đủ 10 số)")
    private String phone;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Quận không được để trống")
    private String district;

    @NotBlank(message = "Thành phố không được để trống")
    private String city;
}