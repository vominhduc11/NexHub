package com.devwonder.warranty_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating warranty claim")
public class WarrantyClaimRequest {
    
    @NotNull(message = "Purchased product ID is required")
    @Positive(message = "Purchased product ID must be positive")
    @Schema(description = "ID of the purchased product", example = "1")
    private Long purchasedProductId;
    
    @NotBlank(message = "Issue description is required")
    @Size(min = 10, max = 1000, message = "Issue description must be between 10 and 1000 characters")
    @Schema(description = "Description of the issue", example = "Device stops working after 6 months of use")
    private String issueDescription;
    
    @Schema(description = "Customer reported issue category", example = "HARDWARE_FAILURE")
    private IssueCategory issueCategory;
    
    @Schema(description = "Priority level of the claim", example = "MEDIUM")
    private Priority priority;
    
    @Size(max = 2000, message = "Customer notes cannot exceed 2000 characters")
    @Schema(description = "Additional notes from customer", example = "Device was handled carefully")
    private String customerNotes;
    
    public enum IssueCategory {
        HARDWARE_FAILURE, SOFTWARE_ISSUE, PHYSICAL_DAMAGE, PERFORMANCE_ISSUE, OTHER
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
}