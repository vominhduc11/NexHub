package com.devwonder.product_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "long_description", columnDefinition = "TEXT")
    private String longDescription;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String specifications;

    @Column(name = "availability_status", length = 50)
    private String availabilityStatus;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "estimated_delivery")
    private String estimatedDelivery;

    @Column(name = "warranty_period")
    private Integer warrantyPeriod;

    @Column(name = "warranty_coverage", columnDefinition = "TEXT")
    private String warrantyCoverage;

    @Column(name = "warranty_conditions", columnDefinition = "TEXT")
    private String warrantyConditions;

    @Column(name = "warranty_excludes", columnDefinition = "TEXT")
    private String warrantyExcludes;

    @Column(name = "warranty_registration_required")
    private Boolean warrantyRegistrationRequired;

    @Column(columnDefinition = "TEXT")
    private String highlights;

    @Column(name = "target_audience", length = 255)
    private String targetAudience;

    @Column(name = "use_cases", columnDefinition = "TEXT")
    private String useCases;

    private Integer popularity;

    private BigDecimal rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(name = "related_product_ids", columnDefinition = "TEXT")
    private String relatedProductIds;

    @Column(columnDefinition = "TEXT")
    private String accessories;

    @Column(name = "seo_title", length = 255)
    private String seoTitle;

    @Column(name = "seo_description", columnDefinition = "TEXT")
    private String seoDescription;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVideo> productVideos;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductFeature> productFeatures;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSerial> productSerials;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}