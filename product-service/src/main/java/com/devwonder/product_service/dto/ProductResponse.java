package com.devwonder.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String subtitle;
    private String description;
    private String categoryName;
    private String availabilityStatus;
    private String estimatedDelivery;
    private Integer warrantyPeriod;
    private String highlights;
    private String targetAudience;
    private Integer popularity;
    private BigDecimal rating;
    private Integer reviewCount;
    private String sku;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}