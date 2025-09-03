package com.devwonder.auth_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResellerDeletedEvent {
    
    private Long accountId;
    private String resellerName;
    private String email;
    private LocalDateTime deletedAt;
    private String reason;
}