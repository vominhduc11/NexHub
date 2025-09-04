package com.devwonder.blog_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlogCategoryRequest {
    
    @NotBlank(message = "Category name is required")
    @Size(max = 255, message = "Category name cannot exceed 255 characters")
    private String name;
    
    @NotBlank(message = "Slug is required")
    @Size(max = 255, message = "Slug cannot exceed 255 characters")
    private String slug;
    
    private String description;
    
    @Size(max = 7, message = "Color should be a valid hex color")
    private String color;
    
    @Size(max = 255, message = "Icon cannot exceed 255 characters")
    private String icon;
    
    private Boolean isVisible = true;
}