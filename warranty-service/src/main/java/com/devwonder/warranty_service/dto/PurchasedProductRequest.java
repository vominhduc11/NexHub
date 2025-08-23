package com.devwonder.warranty_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating/updating purchased product warranty")
public class PurchasedProductRequest {
    
    @NotNull(message = "Purchase date is required")
    @Schema(description = "Date when the product was purchased", example = "2024-01-15")
    private LocalDate purchaseDate;
    
    @NotNull(message = "Expiration date is required")
    @FutureOrPresent(message = "Expiration date must be in the future or present")
    @Schema(description = "Warranty expiration date", example = "2026-01-15")
    private LocalDate expirationDate;
    
    @NotNull(message = "Product serial ID is required")
    @Schema(description = "ID of the product serial", example = "1")
    private Long idProductSerial;
    
    @NotNull(message = "Reseller ID is required")
    @Schema(description = "ID of the reseller who sold the product", example = "1")
    private Long idReseller;
    
    @NotNull(message = "Customer ID is required")
    @Schema(description = "ID of the customer who purchased the product", example = "1")
    private Long idCustomer;
}