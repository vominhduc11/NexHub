package com.devwonder.warranty_service.service;

import com.devwonder.warranty_service.dto.WarrantyClaimRequest;
import com.devwonder.warranty_service.dto.WarrantyClaimResponse;
import com.devwonder.warranty_service.dto.WarrantyStatsResponse;
import com.devwonder.warranty_service.entity.PurchasedProduct;
import com.devwonder.warranty_service.entity.WarrantyClaim;
import com.devwonder.warranty_service.mapper.WarrantyMapper;
import com.devwonder.warranty_service.repository.PurchasedProductRepository;
import com.devwonder.warranty_service.repository.WarrantyClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarrantyClaimService {
    
    private final WarrantyClaimRepository warrantyClaimRepository;
    private final PurchasedProductRepository purchasedProductRepository;
    private final WarrantyMapper warrantyMapper;
    
    // Get all claims
    @Transactional(readOnly = true)
    @Cacheable(value = "warranty-claims", key = "'page:' + #page + ':size:' + #size")
    public Page<WarrantyClaimResponse> getAllClaims(int page, int size) {
        log.info("Fetching all warranty claims - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WarrantyClaim> claims = warrantyClaimRepository.findAll(pageable);
        
        return claims.map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Get claims by customer
    @Transactional(readOnly = true)
    @Cacheable(value = "warranty-claims-by-customer", key = "'customer:' + #customerId + ':page:' + #page + ':size:' + #size")
    public Page<WarrantyClaimResponse> getClaimsByCustomer(Long customerId, int page, int size) {
        log.info("Fetching warranty claims for customer: {} - page: {}, size: {}", customerId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WarrantyClaim> claims = warrantyClaimRepository.findByCustomerId(customerId, pageable);
        
        return claims.map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Get claims by reseller
    @Transactional(readOnly = true)
    public Page<WarrantyClaimResponse> getClaimsByReseller(Long resellerId, int page, int size) {
        log.info("Fetching warranty claims for reseller: {} - page: {}, size: {}", resellerId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WarrantyClaim> claims = warrantyClaimRepository.findByResellerId(resellerId, pageable);
        
        return claims.map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Get claims by status
    @Transactional(readOnly = true)
    @Cacheable(value = "warranty-claims-by-status", key = "'status:' + #status + ':page:' + #page + ':size:' + #size")
    public Page<WarrantyClaimResponse> getClaimsByStatus(WarrantyClaimResponse.ClaimStatus status, int page, int size) {
        log.info("Fetching warranty claims with status: {} - page: {}, size: {}", status, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WarrantyClaim> claims = warrantyClaimRepository.findByStatus(status, pageable);
        
        return claims.map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Get pending claims
    @Transactional(readOnly = true)
    @Cacheable(value = "warranty-claims-pending", key = "'page:' + #page + ':size:' + #size")
    public Page<WarrantyClaimResponse> getPendingClaims(int page, int size) {
        log.info("Fetching pending warranty claims - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WarrantyClaim> claims = warrantyClaimRepository.findPendingClaims(pageable);
        
        return claims.map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Get recent claims
    @Transactional(readOnly = true)
    public Page<WarrantyClaimResponse> getRecentClaims(int days, int page, int size) {
        log.info("Fetching recent warranty claims from last {} days - page: {}, size: {}", days, page, size);
        
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(page, size);
        Page<WarrantyClaim> claims = warrantyClaimRepository.findRecentClaims(fromDate, pageable);
        
        return claims.map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Get overdue claims
    @Transactional(readOnly = true)
    public Page<WarrantyClaimResponse> getOverdueClaims(int days, int page, int size) {
        log.info("Fetching overdue warranty claims (older than {} days) - page: {}, size: {}", days, page, size);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(page, size);
        Page<WarrantyClaim> claims = warrantyClaimRepository.findOverdueClaims(cutoffDate, pageable);
        
        return claims.map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Search claims
    @Transactional(readOnly = true)
    public Page<WarrantyClaimResponse> searchClaims(String keyword, int page, int size) {
        log.info("Searching warranty claims with keyword: '{}' - page: {}, size: {}", keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WarrantyClaim> claims = warrantyClaimRepository.searchByIssueDescription(keyword, pageable);
        
        return claims.map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Get claims by date range
    @Transactional(readOnly = true)
    public Page<WarrantyClaimResponse> getClaimsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        log.info("Fetching warranty claims between {} and {} - page: {}, size: {}", startDate, endDate, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WarrantyClaim> claims = warrantyClaimRepository.findByDateRange(startDate, endDate, pageable);
        
        return claims.map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Get claim by ID
    @Transactional(readOnly = true)
    public Optional<WarrantyClaimResponse> getClaimById(Long id) {
        log.info("Fetching warranty claim by ID: {}", id);
        
        return warrantyClaimRepository.findById(id)
            .map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Get claim by claim number
    @Transactional(readOnly = true)
    public Optional<WarrantyClaimResponse> getClaimByNumber(String claimNumber) {
        log.info("Fetching warranty claim by number: {}", claimNumber);
        
        return warrantyClaimRepository.findByClaimNumber(claimNumber)
            .map(warrantyMapper::toWarrantyClaimResponse);
    }
    
    // Create new claim
    @CacheEvict(value = {"warranty-claims", "warranty-claims-by-customer", "warranty-claims-by-status", "warranty-claims-pending", "warranty-stats"}, allEntries = true)
    public WarrantyClaimResponse createClaim(WarrantyClaimRequest request) {
        log.info("Creating new warranty claim for purchased product: {}", request.getPurchasedProductId());
        
        // Validate purchased product exists
        PurchasedProduct purchasedProduct = purchasedProductRepository.findById(request.getPurchasedProductId())
            .orElseThrow(() -> new IllegalArgumentException("Purchased product not found with ID: " + request.getPurchasedProductId()));
        
        // Check if warranty is still active
        if (purchasedProduct.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Warranty has expired for this product");
        }
        
        // Check if there are existing active claims for this product
        Long activeClaims = warrantyClaimRepository.countActiveClaims(request.getPurchasedProductId());
        if (activeClaims > 0) {
            throw new IllegalArgumentException("There is already an active warranty claim for this product");
        }
        
        // Generate claim number
        Optional<String> latestClaimNumber = warrantyClaimRepository.findLatestClaimNumber();
        String claimNumber = warrantyMapper.generateClaimNumber(latestClaimNumber.orElse(null));
        
        WarrantyClaim warrantyClaim = warrantyMapper.toWarrantyClaimEntity(request, claimNumber);
        WarrantyClaim savedClaim = warrantyClaimRepository.save(warrantyClaim);
        
        log.info("Warranty claim created successfully with number: {}", savedClaim.getClaimNumber());
        return warrantyMapper.toWarrantyClaimResponse(savedClaim);
    }
    
    // Update claim status
    @CacheEvict(value = {"warranty-claims", "warranty-claims-by-customer", "warranty-claims-by-status", "warranty-claims-pending", "warranty-stats"}, allEntries = true)
    public WarrantyClaimResponse updateClaimStatus(Long id, WarrantyClaimResponse.ClaimStatus status, String internalNotes) {
        log.info("Updating warranty claim status: {} to {}", id, status);
        
        WarrantyClaim claim = warrantyClaimRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Warranty claim not found with id: " + id));
        
        WarrantyClaimResponse.ClaimStatus oldStatus = claim.getStatus();
        claim.setStatus(status);
        claim.setInternalNotes(internalNotes);
        
        // Set resolved date if claim is completed or rejected
        if (status == WarrantyClaimResponse.ClaimStatus.COMPLETED || 
            status == WarrantyClaimResponse.ClaimStatus.REJECTED) {
            claim.setResolvedAt(LocalDateTime.now());
        }
        
        WarrantyClaim updatedClaim = warrantyClaimRepository.save(claim);
        log.info("Warranty claim status updated from {} to {}: {}", oldStatus, status, updatedClaim.getClaimNumber());
        
        return warrantyMapper.toWarrantyClaimResponse(updatedClaim);
    }
    
    // Add resolution notes
    public WarrantyClaimResponse addResolutionNotes(Long id, String resolutionNotes) {
        log.info("Adding resolution notes to warranty claim: {}", id);
        
        WarrantyClaim claim = warrantyClaimRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Warranty claim not found with id: " + id));
        
        claim.setResolutionNotes(resolutionNotes);
        WarrantyClaim updatedClaim = warrantyClaimRepository.save(claim);
        
        log.info("Resolution notes added to warranty claim: {}", updatedClaim.getClaimNumber());
        return warrantyMapper.toWarrantyClaimResponse(updatedClaim);
    }
    
    // Delete claim
    @CacheEvict(value = {"warranty-claims", "warranty-claims-by-customer", "warranty-claims-by-status", "warranty-claims-pending", "warranty-stats"}, allEntries = true)
    public void deleteClaim(Long id) {
        log.info("Deleting warranty claim with ID: {}", id);
        
        WarrantyClaim claim = warrantyClaimRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Warranty claim not found with id: " + id));
        
        warrantyClaimRepository.delete(claim);
        log.info("Warranty claim deleted successfully: {}", claim.getClaimNumber());
    }
    
    // Get warranty statistics
    @Transactional(readOnly = true)
    @Cacheable(value = "warranty-stats", key = "'stats'")
    public WarrantyStatsResponse getWarrantyStats() {
        log.info("Generating warranty statistics");
        
        // Get warranty counts
        Long totalActive = purchasedProductRepository.countActiveWarranties();
        Long totalExpired = purchasedProductRepository.countExpiredWarranties();
        LocalDate expiringSoonDate = LocalDate.now().plusDays(30);
        Long totalExpiringSoon = purchasedProductRepository.countWarrantiesExpiringSoon(expiringSoonDate);
        
        // Get claim counts
        Long totalClaims = warrantyClaimRepository.countTotalClaims();
        Long totalPending = warrantyClaimRepository.countByStatus(WarrantyClaimResponse.ClaimStatus.PENDING);
        Long totalApproved = warrantyClaimRepository.countByStatus(WarrantyClaimResponse.ClaimStatus.APPROVED);
        Long totalRejected = warrantyClaimRepository.countByStatus(WarrantyClaimResponse.ClaimStatus.REJECTED);
        Long totalCompleted = warrantyClaimRepository.countByStatus(WarrantyClaimResponse.ClaimStatus.COMPLETED);
        
        // Calculate metrics
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
    
    // Get claims needing attention
    @Transactional(readOnly = true)
    public List<WarrantyClaimResponse> getClaimsNeedingAttention() {
        log.info("Fetching warranty claims needing attention");
        
        List<WarrantyClaim> claims = warrantyClaimRepository.findClaimsNeedingAttention();
        return claims.stream()
            .map(warrantyMapper::toWarrantyClaimResponse)
            .toList();
    }
}