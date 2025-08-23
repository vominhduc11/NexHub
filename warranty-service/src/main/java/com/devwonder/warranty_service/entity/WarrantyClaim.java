package com.devwonder.warranty_service.entity;

import com.devwonder.warranty_service.dto.WarrantyClaimRequest;
import com.devwonder.warranty_service.dto.WarrantyClaimResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "warranty_claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyClaim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "claim_number", unique = true, nullable = false)
    private String claimNumber;
    
    @Column(name = "purchased_product_id", nullable = false)
    private Long purchasedProductId;
    
    @Column(name = "issue_description", columnDefinition = "TEXT", nullable = false)
    private String issueDescription;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "issue_category")
    private WarrantyClaimRequest.IssueCategory issueCategory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private WarrantyClaimRequest.Priority priority;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WarrantyClaimResponse.ClaimStatus status = WarrantyClaimResponse.ClaimStatus.PENDING;
    
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;
    
    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;
    
    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;
    
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchased_product_id", insertable = false, updatable = false)
    private PurchasedProduct purchasedProduct;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}