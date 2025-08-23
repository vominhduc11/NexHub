package com.devwonder.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {
    
    @NotBlank(message = "Image URL is required")
    private String imageUrl;
    
    private String altText;
    
    @NotNull(message = "Display order is required")
    private Integer displayOrder;
    
    private Boolean isPrimary = false;
}