package com.devwonder.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {
    private Long accountId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}