package com.devwonder.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long accountId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}