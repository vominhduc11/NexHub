package com.devwonder.user_service.dto;

import com.devwonder.user_service.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResellerResponse {
    private Long accountId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String district;
    private String city;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ApprovalStatus approvalStatus;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
}