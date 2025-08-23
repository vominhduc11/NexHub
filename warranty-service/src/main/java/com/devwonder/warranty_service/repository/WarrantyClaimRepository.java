package com.devwonder.warranty_service.repository;

import com.devwonder.warranty_service.entity.WarrantyClaim;
import com.devwonder.warranty_service.dto.WarrantyClaimResponse;
import com.devwonder.warranty_service.dto.WarrantyClaimRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarrantyClaimRepository extends JpaRepository<WarrantyClaim, Long> {
    
    // Override default methods to exclude soft deleted
    @Override
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.deletedAt IS NULL")
    Page<WarrantyClaim> findAll(Pageable pageable);
    
    @Override
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.id = :id AND wc.deletedAt IS NULL")
    Optional<WarrantyClaim> findById(@Param("id") Long id);
    
    // Find by claim number
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.claimNumber = :claimNumber AND wc.deletedAt IS NULL")
    Optional<WarrantyClaim> findByClaimNumber(@Param("claimNumber") String claimNumber);
    
    // Find claims by purchased product
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.purchasedProductId = :purchasedProductId AND wc.deletedAt IS NULL ORDER BY wc.submittedAt DESC")
    Page<WarrantyClaim> findByPurchasedProductId(@Param("purchasedProductId") Long purchasedProductId, Pageable pageable);
    
    // Find claims by customer ID (via purchased product)
    @Query("SELECT wc FROM WarrantyClaim wc JOIN wc.purchasedProduct pp WHERE pp.idCustomer = :customerId AND wc.deletedAt IS NULL AND pp.deletedAt IS NULL ORDER BY wc.submittedAt DESC")
    Page<WarrantyClaim> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
    
    // Find claims by reseller ID (via purchased product)
    @Query("SELECT wc FROM WarrantyClaim wc JOIN wc.purchasedProduct pp WHERE pp.idReseller = :resellerId AND wc.deletedAt IS NULL AND pp.deletedAt IS NULL ORDER BY wc.submittedAt DESC")
    Page<WarrantyClaim> findByResellerId(@Param("resellerId") Long resellerId, Pageable pageable);
    
    // Find claims by status
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.status = :status AND wc.deletedAt IS NULL ORDER BY wc.submittedAt DESC")
    Page<WarrantyClaim> findByStatus(@Param("status") WarrantyClaimResponse.ClaimStatus status, Pageable pageable);
    
    // Find claims by priority
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.priority = :priority AND wc.deletedAt IS NULL ORDER BY wc.submittedAt DESC")
    Page<WarrantyClaim> findByPriority(@Param("priority") WarrantyClaimRequest.Priority priority, Pageable pageable);
    
    // Find claims by issue category
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.issueCategory = :category AND wc.deletedAt IS NULL ORDER BY wc.submittedAt DESC")
    Page<WarrantyClaim> findByIssueCategory(@Param("category") WarrantyClaimRequest.IssueCategory category, Pageable pageable);
    
    // Find pending claims (for admin dashboard)
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.status = 'PENDING' AND wc.deletedAt IS NULL ORDER BY wc.priority DESC, wc.submittedAt ASC")
    Page<WarrantyClaim> findPendingClaims(Pageable pageable);
    
    // Find recent claims
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.submittedAt >= :fromDate AND wc.deletedAt IS NULL ORDER BY wc.submittedAt DESC")
    Page<WarrantyClaim> findRecentClaims(@Param("fromDate") LocalDateTime fromDate, Pageable pageable);
    
    // Find claims by date range
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.submittedAt BETWEEN :startDate AND :endDate AND wc.deletedAt IS NULL ORDER BY wc.submittedAt DESC")
    Page<WarrantyClaim> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // Find overdue claims (pending for more than X days)
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.status IN ('PENDING', 'IN_REVIEW') " +
           "AND wc.submittedAt <= :cutoffDate AND wc.deletedAt IS NULL ORDER BY wc.submittedAt ASC")
    Page<WarrantyClaim> findOverdueClaims(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);
    
    // Search claims by issue description
    @Query("SELECT wc FROM WarrantyClaim wc WHERE LOWER(wc.issueDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "AND wc.deletedAt IS NULL ORDER BY wc.submittedAt DESC")
    Page<WarrantyClaim> searchByIssueDescription(@Param("keyword") String keyword, Pageable pageable);
    
    // Find claims needing attention (high priority pending)
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.priority IN ('HIGH', 'URGENT') " +
           "AND wc.status IN ('PENDING', 'IN_REVIEW') AND wc.deletedAt IS NULL ORDER BY wc.priority DESC, wc.submittedAt ASC")
    List<WarrantyClaim> findClaimsNeedingAttention();
    
    // Statistics queries
    @Query("SELECT COUNT(wc) FROM WarrantyClaim wc WHERE wc.deletedAt IS NULL")
    Long countTotalClaims();
    
    @Query("SELECT COUNT(wc) FROM WarrantyClaim wc WHERE wc.status = :status AND wc.deletedAt IS NULL")
    Long countByStatus(@Param("status") WarrantyClaimResponse.ClaimStatus status);
    
    @Query("SELECT COUNT(wc) FROM WarrantyClaim wc WHERE wc.priority = :priority AND wc.deletedAt IS NULL")
    Long countByPriority(@Param("priority") WarrantyClaimRequest.Priority priority);
    
    @Query("SELECT COUNT(wc) FROM WarrantyClaim wc WHERE wc.issueCategory = :category AND wc.deletedAt IS NULL")
    Long countByIssueCategory(@Param("category") WarrantyClaimRequest.IssueCategory category);
    
    @Query("SELECT COUNT(wc) FROM WarrantyClaim wc WHERE wc.submittedAt >= :fromDate AND wc.deletedAt IS NULL")
    Long countRecentClaims(@Param("fromDate") LocalDateTime fromDate);
    
    // Average resolution time in days (PostgreSQL compatible)
    @Query("SELECT AVG(wc.resolvedAt - wc.submittedAt) FROM WarrantyClaim wc WHERE wc.resolvedAt IS NOT NULL AND wc.deletedAt IS NULL")
    Double calculateAverageResolutionDays();
    
    // Approval rate calculation
    @Query("SELECT (COUNT(wc) * 100.0 / (SELECT COUNT(wc2) FROM WarrantyClaim wc2 WHERE wc2.status IN ('APPROVED', 'REJECTED', 'COMPLETED') AND wc2.deletedAt IS NULL)) " +
           "FROM WarrantyClaim wc WHERE wc.status IN ('APPROVED', 'COMPLETED') AND wc.deletedAt IS NULL")
    Double calculateApprovalRate();
    
    // Check if customer has existing claims for product
    @Query("SELECT COUNT(wc) FROM WarrantyClaim wc WHERE wc.purchasedProductId = :purchasedProductId " +
           "AND wc.status IN ('PENDING', 'IN_REVIEW', 'APPROVED', 'IN_REPAIR') AND wc.deletedAt IS NULL")
    Long countActiveClaims(@Param("purchasedProductId") Long purchasedProductId);
    
    // Find latest claim number for generating new ones
    @Query("SELECT wc.claimNumber FROM WarrantyClaim wc WHERE wc.deletedAt IS NULL ORDER BY wc.id DESC LIMIT 1")
    Optional<String> findLatestClaimNumber();
    
    // Soft delete methods
    @Modifying
    @Query("UPDATE WarrantyClaim wc SET wc.deletedAt = CURRENT_TIMESTAMP WHERE wc.id = :id")
    void softDelete(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE WarrantyClaim wc SET wc.deletedAt = NULL WHERE wc.id = :id")
    void restore(@Param("id") Long id);
    
    @Query("SELECT wc FROM WarrantyClaim wc WHERE wc.deletedAt IS NULL ORDER BY wc.submittedAt DESC")
    Page<WarrantyClaim> findAllActive(Pageable pageable);
}