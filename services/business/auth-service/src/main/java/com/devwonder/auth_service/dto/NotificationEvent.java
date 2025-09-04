package com.devwonder.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    
    private String eventType;
    private Long accountId;
    private String username;
    private String email;
    private String name;
    private String subject;
    private String message;
    private LocalDateTime timestamp;
    
}