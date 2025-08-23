package com.devwonder.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String name;
    
    @Size(max = 500, message = "Subtitle cannot exceed 500 characters")
    private String subtitle;
    
    private String description;
    
    private String longDescription;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private String specifications;
    
    private String availabilityStatus;
    
    private LocalDateTime releaseDate;
    
    private String estimatedDelivery;
    
    private Integer warrantyPeriod;
    
    private String warrantyCoverage;
    
    private String warrantyConditions;
    
    private String warrantyExcludes;
    
    private Boolean warrantyRegistrationRequired;
    
    private String highlights;
    
    private String targetAudience;
    
    private String useCases;
    
    private Integer popularity;
    
    private BigDecimal rating;
    
    private Integer reviewCount;
    
    private String tags;
    
    @NotBlank(message = "SKU is required")
    private String sku;
    
    private String relatedProductIds;
    
    private String accessories;
    
    private String seoTitle;
    
    private String seoDescription;
    
    private LocalDateTime publishedAt;
}