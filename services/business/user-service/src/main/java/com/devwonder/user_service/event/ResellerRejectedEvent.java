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
public class ResellerRejectedEvent {
    private Long accountId;
    private String resellerName;
    private String email;
    private Long rejectedBy;
    private LocalDateTime rejectedAt;
    private String rejectionReason;

    public static ResellerRejectedEvent of(Long accountId, String resellerName, String email, Long rejectedBy, String rejectionReason) {
        return ResellerRejectedEvent.builder()
                .accountId(accountId)
                .resellerName(resellerName)
                .email(email)
                .rejectedBy(rejectedBy)
                .rejectedAt(LocalDateTime.now())
                .rejectionReason(rejectionReason)
                .build();
    }
}