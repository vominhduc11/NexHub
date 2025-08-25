package com.devwonder.product_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {
    
    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    @Pattern(regexp = "^https?://.*\\.(jpg|jpeg|png|gif|webp)$", 
             message = "Image URL must be a valid HTTP/HTTPS URL ending with jpg, jpeg, png, gif, or webp")
    private String imageUrl;
    
    @Size(max = 255, message = "Alt text cannot exceed 255 characters")
    private String altText;
    
    @NotNull(message = "Display order is required")
    @Min(value = 1, message = "Display order must be at least 1")
    private Integer displayOrder;
    
    private Boolean isPrimary = false;
}