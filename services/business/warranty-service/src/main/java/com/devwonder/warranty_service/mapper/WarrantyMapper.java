package com.devwonder.warranty_service.mapper;

import com.devwonder.warranty_service.dto.*;
import com.devwonder.warranty_service.entity.PurchasedProduct;
import com.devwonder.warranty_service.entity.WarrantyClaim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface WarrantyMapper {
    
    @Mapping(target = "warrantyStatus", source = "expirationDate", qualifiedByName = "determineWarrantyStatus")
    PurchasedProductResponse toPurchasedProductResponse(PurchasedProduct purchasedProduct);
    
    WarrantyClaimResponse toWarrantyClaimResponse(WarrantyClaim warrantyClaim);
    
    @Mapping(target = "warrantyRemainingDays", source = "expirationDate", qualifiedByName = "calculateRemainingDays")
    PurchasedProduct toPurchasedProductEntity(PurchasedProductRequest request);
    
    @Mapping(target = "claimNumber", source = "claimNumber")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "submittedAt", expression = "java(java.time.LocalDateTime.now())")
    WarrantyClaim toWarrantyClaimEntity(WarrantyClaimRequest request, String claimNumber);
    
    @Named("determineWarrantyStatus")
    default PurchasedProductResponse.WarrantyStatus determineWarrantyStatus(LocalDate expirationDate) {
        if (expirationDate == null) return PurchasedProductResponse.WarrantyStatus.EXPIRED;
        
        LocalDate now = LocalDate.now();
        if (expirationDate.isBefore(now)) {
            return PurchasedProductResponse.WarrantyStatus.EXPIRED;
        } else if (expirationDate.isBefore(now.plusDays(30))) {
            return PurchasedProductResponse.WarrantyStatus.EXPIRING_SOON;
        } else {
            return PurchasedProductResponse.WarrantyStatus.ACTIVE;
        }
    }
    
    @Named("calculateRemainingDays")
    default Integer calculateRemainingDays(LocalDate expirationDate) {
        if (expirationDate == null) return 0;
        
        LocalDate now = LocalDate.now();
        if (expirationDate.isBefore(now)) {
            return 0;
        }
        
        return (int) ChronoUnit.DAYS.between(now, expirationDate);
    }
    
    default String generateClaimNumber(String latestClaimNumber) {
        if (latestClaimNumber == null || latestClaimNumber.isEmpty()) {
            return "WC-" + LocalDate.now().getYear() + "-001";
        }
        
        // Extract year and number from latest claim number (format: WC-YYYY-XXX)
        String[] parts = latestClaimNumber.split("-");
        if (parts.length != 3) {
            return "WC-" + LocalDate.now().getYear() + "-001";
        }
        
        int currentYear = LocalDate.now().getYear();
        int lastYear = Integer.parseInt(parts[1]);
        int lastNumber = Integer.parseInt(parts[2]);
        
        if (currentYear > lastYear) {
            // New year, reset counter
            return "WC-" + currentYear + "-001";
        } else {
            // Same year, increment counter
            return String.format("WC-%d-%03d", currentYear, lastNumber + 1);
        }
    }
}