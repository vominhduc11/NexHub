package com.devwonder.warranty_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for warranty statistics")
public class WarrantyStatsResponse {
    
    @Schema(description = "Total number of active warranties")
    private Long totalActiveWarranties;
    
    @Schema(description = "Total number of expired warranties")
    private Long totalExpiredWarranties;
    
    @Schema(description = "Total number of warranties expiring soon (within 30 days)")
    private Long totalExpiringSoonWarranties;
    
    @Schema(description = "Total number of warranty claims")
    private Long totalClaims;
    
    @Schema(description = "Total number of pending warranty claims")
    private Long totalPendingClaims;
    
    @Schema(description = "Total number of approved warranty claims")
    private Long totalApprovedClaims;
    
    @Schema(description = "Total number of rejected warranty claims")
    private Long totalRejectedClaims;
    
    @Schema(description = "Total number of completed warranty claims")
    private Long totalCompletedClaims;
    
    @Schema(description = "Average claim resolution time in days")
    private Double averageResolutionDays;
    
    @Schema(description = "Claim approval rate as percentage")
    private Double claimApprovalRate;
}