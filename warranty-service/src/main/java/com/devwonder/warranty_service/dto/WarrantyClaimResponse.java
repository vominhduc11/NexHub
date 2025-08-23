package com.devwonder.warranty_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for warranty claim information")
public class WarrantyClaimResponse {
    
    @Schema(description = "Unique identifier of the warranty claim", example = "1")
    private Long id;
    
    @Schema(description = "Claim reference number", example = "WC-2024-001")
    private String claimNumber;
    
    @Schema(description = "ID of the purchased product", example = "1")
    private Long purchasedProductId;
    
    @Schema(description = "Description of the issue", example = "Device stops working after 6 months of use")
    private String issueDescription;
    
    @Schema(description = "Customer reported issue category", example = "HARDWARE_FAILURE")
    private WarrantyClaimRequest.IssueCategory issueCategory;
    
    @Schema(description = "Priority level of the claim", example = "MEDIUM")
    private WarrantyClaimRequest.Priority priority;
    
    @Schema(description = "Current status of the claim", example = "PENDING")
    private ClaimStatus status;
    
    @Schema(description = "Resolution details if claim is resolved")
    private String resolutionNotes;
    
    @Schema(description = "Additional notes from customer")
    private String customerNotes;
    
    @Schema(description = "Internal notes from support team")
    private String internalNotes;
    
    @Schema(description = "Date when claim was submitted", example = "2024-08-23T10:30:00")
    private LocalDateTime submittedAt;
    
    @Schema(description = "Date when claim was resolved", example = "2024-08-25T15:45:00")
    private LocalDateTime resolvedAt;
    
    @Schema(description = "Creation timestamp", example = "2024-08-23T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", example = "2024-08-23T10:30:00")
    private LocalDateTime updatedAt;
    
    // Related information
    @Schema(description = "Product information")
    private PurchasedProductResponse.ProductSummary product;
    
    @Schema(description = "Customer information")
    private PurchasedProductResponse.CustomerSummary customer;
    
    public enum ClaimStatus {
        PENDING, IN_REVIEW, APPROVED, REJECTED, IN_REPAIR, COMPLETED, CANCELLED
    }
}