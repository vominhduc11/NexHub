package com.devwonder.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVideoRequest {
    
    @NotBlank(message = "Video URL is required")
    private String videoUrl;
    
    private String thumbnailUrl;
    
    private String title;
    
    private String description;
    
    @NotNull(message = "Display order is required")
    private Integer displayOrder;
    
    private Integer duration;
}