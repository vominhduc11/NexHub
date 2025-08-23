package com.devwonder.warranty_service.mapper;

import com.devwonder.warranty_service.dto.*;
import com.devwonder.warranty_service.entity.PurchasedProduct;
import com.devwonder.warranty_service.entity.WarrantyClaim;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class WarrantyMapper {
    
    public PurchasedProductResponse toPurchasedProductResponse(PurchasedProduct purchasedProduct) {
        if (purchasedProduct == null) return null;
        
        PurchasedProductResponse response = new PurchasedProductResponse();
        response.setId(purchasedProduct.getId());
        response.setPurchaseDate(purchasedProduct.getPurchaseDate());
        response.setExpirationDate(purchasedProduct.getExpirationDate());
        response.setWarrantyRemainingDays(purchasedProduct.getWarrantyRemainingDays());
        response.setWarrantyStatus(determineWarrantyStatus(purchasedProduct.getExpirationDate()));
        response.setIdProductSerial(purchasedProduct.getIdProductSerial());
        response.setIdReseller(purchasedProduct.getIdReseller());
        response.setIdCustomer(purchasedProduct.getIdCustomer());
        response.setCreatedAt(purchasedProduct.getCreatedAt());
        response.setUpdatedAt(purchasedProduct.getUpdatedAt());
        
        return response;
    }
    
    public WarrantyClaimResponse toWarrantyClaimResponse(WarrantyClaim warrantyClaim) {
        if (warrantyClaim == null) return null;
        
        WarrantyClaimResponse response = new WarrantyClaimResponse();
        response.setId(warrantyClaim.getId());
        response.setClaimNumber(warrantyClaim.getClaimNumber());
        response.setPurchasedProductId(warrantyClaim.getPurchasedProductId());
        response.setIssueDescription(warrantyClaim.getIssueDescription());
        response.setIssueCategory(warrantyClaim.getIssueCategory());
        response.setPriority(warrantyClaim.getPriority());
        response.setStatus(warrantyClaim.getStatus());
        response.setResolutionNotes(warrantyClaim.getResolutionNotes());
        response.setCustomerNotes(warrantyClaim.getCustomerNotes());
        response.setInternalNotes(warrantyClaim.getInternalNotes());
        response.setSubmittedAt(warrantyClaim.getSubmittedAt());
        response.setResolvedAt(warrantyClaim.getResolvedAt());
        response.setCreatedAt(warrantyClaim.getCreatedAt());
        response.setUpdatedAt(warrantyClaim.getUpdatedAt());
        
        return response;
    }
    
    public PurchasedProduct toPurchasedProductEntity(PurchasedProductRequest request) {
        if (request == null) return null;
        
        PurchasedProduct purchasedProduct = new PurchasedProduct();
        purchasedProduct.setPurchaseDate(request.getPurchaseDate());
        purchasedProduct.setExpirationDate(request.getExpirationDate());
        purchasedProduct.setWarrantyRemainingDays(calculateRemainingDays(request.getExpirationDate()));
        purchasedProduct.setIdProductSerial(request.getIdProductSerial());
        purchasedProduct.setIdReseller(request.getIdReseller());
        purchasedProduct.setIdCustomer(request.getIdCustomer());
        
        return purchasedProduct;
    }
    
    public WarrantyClaim toWarrantyClaimEntity(WarrantyClaimRequest request, String claimNumber) {
        if (request == null) return null;
        
        WarrantyClaim warrantyClaim = new WarrantyClaim();
        warrantyClaim.setClaimNumber(claimNumber);
        warrantyClaim.setPurchasedProductId(request.getPurchasedProductId());
        warrantyClaim.setIssueDescription(request.getIssueDescription());
        warrantyClaim.setIssueCategory(request.getIssueCategory());
        warrantyClaim.setPriority(request.getPriority() != null ? request.getPriority() : WarrantyClaimRequest.Priority.MEDIUM);
        warrantyClaim.setStatus(WarrantyClaimResponse.ClaimStatus.PENDING);
        warrantyClaim.setCustomerNotes(request.getCustomerNotes());
        warrantyClaim.setSubmittedAt(LocalDateTime.now());
        
        return warrantyClaim;
    }
    
    private PurchasedProductResponse.WarrantyStatus determineWarrantyStatus(LocalDate expirationDate) {
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
    
    public Integer calculateRemainingDays(LocalDate expirationDate) {
        if (expirationDate == null) return 0;
        
        LocalDate now = LocalDate.now();
        if (expirationDate.isBefore(now)) {
            return 0;
        }
        
        return (int) ChronoUnit.DAYS.between(now, expirationDate);
    }
    
    public String generateClaimNumber(String latestClaimNumber) {
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