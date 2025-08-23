package com.devwonder.warranty_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for purchased product warranty information")
public class PurchasedProductResponse {
    
    @Schema(description = "Unique identifier of the purchased product", example = "1")
    private Long id;
    
    @Schema(description = "Date when the product was purchased", example = "2024-01-15")
    private LocalDate purchaseDate;
    
    @Schema(description = "Warranty expiration date", example = "2026-01-15")
    private LocalDate expirationDate;
    
    @Schema(description = "Number of warranty days remaining", example = "365")
    private Integer warrantyRemainingDays;
    
    @Schema(description = "Warranty status", example = "ACTIVE")
    private WarrantyStatus warrantyStatus;
    
    @Schema(description = "ID of the product serial", example = "1")
    private Long idProductSerial;
    
    @Schema(description = "ID of the reseller who sold the product", example = "1")
    private Long idReseller;
    
    @Schema(description = "ID of the customer who purchased the product", example = "1")
    private Long idCustomer;
    
    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    // Additional product information (from product service)
    @Schema(description = "Product information")
    private ProductSummary product;
    
    @Schema(description = "Customer information")
    private CustomerSummary customer;
    
    @Schema(description = "Reseller information")
    private ResellerSummary reseller;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSummary {
        private String name;
        private String model;
        private String brand;
        private String serialNumber;
        private String imageUrl;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSummary {
        private String fullName;
        private String email;
        private String phone;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResellerSummary {
        private String businessName;
        private String contactEmail;
        private String contactPhone;
    }
    
    public enum WarrantyStatus {
        ACTIVE, EXPIRING_SOON, EXPIRED
    }
}