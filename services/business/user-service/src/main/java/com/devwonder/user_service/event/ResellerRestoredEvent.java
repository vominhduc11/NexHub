package com.devwonder.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResellerRestoredEvent {
    
    private Long accountId;
    private String resellerName;
    private String email;
    private LocalDateTime restoredAt;
    private String reason;
    
    public static ResellerRestoredEvent of(Long accountId, String resellerName, String email, String reason) {
        return ResellerRestoredEvent.builder()
                .accountId(accountId)
                .resellerName(resellerName)
                .email(email)
                .restoredAt(LocalDateTime.now())
                .reason(reason != null ? reason : "Admin restoration")
                .build();
    }
}