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
public class ResellerDeletedEvent {
    
    private Long accountId;
    private String resellerName;
    private String email;
    private LocalDateTime deletedAt;
    private String reason;
    
    public static ResellerDeletedEvent of(Long accountId, String resellerName, String email, String reason) {
        return ResellerDeletedEvent.builder()
                .accountId(accountId)
                .resellerName(resellerName)
                .email(email)
                .deletedAt(LocalDateTime.now())
                .reason(reason != null ? reason : "Admin deletion")
                .build();
    }
}