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
public class ResellerApprovedEvent {
    private Long accountId;
    private String resellerName;
    private String email;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String reason;

    public static ResellerApprovedEvent of(Long accountId, String resellerName, String email, Long approvedBy, String reason) {
        return ResellerApprovedEvent.builder()
                .accountId(accountId)
                .resellerName(resellerName)
                .email(email)
                .approvedBy(approvedBy)
                .approvedAt(LocalDateTime.now())
                .reason(reason != null ? reason : "Account approved by admin")
                .build();
    }
}