package com.devwonder.warranty_service.service;

import com.devwonder.warranty_service.dto.WarrantyStatsResponse;
import com.devwonder.warranty_service.dto.WarrantyClaimResponse;
import com.devwonder.warranty_service.repository.PurchasedProductRepository;
import com.devwonder.warranty_service.repository.WarrantyClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WarrantyStatsService {
    
    private final WarrantyClaimRepository warrantyClaimRepository;
    private final PurchasedProductRepository purchasedProductRepository;
    
    @Cacheable(value = "warranty-stats", key = "'stats'")
    public WarrantyStatsResponse getWarrantyStats() {
        log.info("Generating warranty statistics");
        
        Long totalActive = purchasedProductRepository.countActiveWarranties();
        Long totalExpired = purchasedProductRepository.countExpiredWarranties();
        LocalDate expiringSoonDate = LocalDate.now().plusDays(30);
        Long totalExpiringSoon = purchasedProductRepository.countWarrantiesExpiringSoon(expiringSoonDate);
        
        Long totalClaims = warrantyClaimRepository.countTotalClaims();
        Long totalPending = warrantyClaimRepository.countByStatus(WarrantyClaimResponse.ClaimStatus.PENDING);
        Long totalApproved = warrantyClaimRepository.countByStatus(WarrantyClaimResponse.ClaimStatus.APPROVED);
        Long totalRejected = warrantyClaimRepository.countByStatus(WarrantyClaimResponse.ClaimStatus.REJECTED);
        Long totalCompleted = warrantyClaimRepository.countByStatus(WarrantyClaimResponse.ClaimStatus.COMPLETED);
        
        Double averageResolutionDays = warrantyClaimRepository.calculateAverageResolutionDays();
        Double claimApprovalRate = warrantyClaimRepository.calculateApprovalRate();
        
        WarrantyStatsResponse stats = new WarrantyStatsResponse();
        stats.setTotalActiveWarranties(totalActive);
        stats.setTotalExpiredWarranties(totalExpired);
        stats.setTotalExpiringSoonWarranties(totalExpiringSoon);
        stats.setTotalClaims(totalClaims);
        stats.setTotalPendingClaims(totalPending);
        stats.setTotalApprovedClaims(totalApproved);
        stats.setTotalRejectedClaims(totalRejected);
        stats.setTotalCompletedClaims(totalCompleted);
        stats.setAverageResolutionDays(averageResolutionDays != null ? averageResolutionDays : 0.0);
        stats.setClaimApprovalRate(claimApprovalRate != null ? claimApprovalRate : 0.0);
        
        log.info("Warranty statistics generated successfully");
        return stats;
    }
}