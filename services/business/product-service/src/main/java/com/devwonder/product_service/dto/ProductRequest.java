package com.devwonder.product_service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    private String name;
    
    @Size(max = 500, message = "Subtitle cannot exceed 500 characters")
    private String subtitle;
    
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @Size(max = 5000, message = "Long description cannot exceed 5000 characters")
    private String longDescription;
    
    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;
    
    @Size(max = 3000, message = "Specifications cannot exceed 3000 characters")
    private String specifications;
    
    @Pattern(regexp = "IN_STOCK|OUT_OF_STOCK|PRE_ORDER|DISCONTINUED", 
             message = "Availability status must be IN_STOCK, OUT_OF_STOCK, PRE_ORDER, or DISCONTINUED")
    private String availabilityStatus;
    
    private LocalDateTime releaseDate;
    
    @Size(max = 255, message = "Estimated delivery cannot exceed 255 characters")
    private String estimatedDelivery;
    
    @Min(value = 0, message = "Warranty period cannot be negative")
    @Max(value = 120, message = "Warranty period cannot exceed 120 months")
    private Integer warrantyPeriod;
    
    @Size(max = 1000, message = "Warranty coverage cannot exceed 1000 characters")
    private String warrantyCoverage;
    
    @Size(max = 1000, message = "Warranty conditions cannot exceed 1000 characters")
    private String warrantyConditions;
    
    @Size(max = 1000, message = "Warranty excludes cannot exceed 1000 characters")
    private String warrantyExcludes;
    
    private Boolean warrantyRegistrationRequired;
    
    @Size(max = 2000, message = "Highlights cannot exceed 2000 characters")
    private String highlights;
    
    @Size(max = 500, message = "Target audience cannot exceed 500 characters")
    private String targetAudience;
    
    @Size(max = 1000, message = "Use cases cannot exceed 1000 characters")
    private String useCases;
    
    @Min(value = 0, message = "Popularity cannot be negative")
    @Max(value = 100, message = "Popularity cannot exceed 100")
    private Integer popularity;
    
    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private BigDecimal rating;
    
    @Min(value = 0, message = "Review count cannot be negative")
    private Integer reviewCount;
    
    @Size(max = 500, message = "Tags cannot exceed 500 characters")
    private String tags;
    
    @NotBlank(message = "SKU is required")
    @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Z0-9-_]+$", message = "SKU can only contain uppercase letters, numbers, hyphens, and underscores")
    private String sku;
    
    @Size(max = 500, message = "Related product IDs cannot exceed 500 characters")
    private String relatedProductIds;
    
    @Size(max = 1000, message = "Accessories cannot exceed 1000 characters")
    private String accessories;
    
    @Size(max = 255, message = "SEO title cannot exceed 255 characters")
    private String seoTitle;
    
    @Size(max = 500, message = "SEO description cannot exceed 500 characters")
    private String seoDescription;
    
    private LocalDateTime publishedAt;
}