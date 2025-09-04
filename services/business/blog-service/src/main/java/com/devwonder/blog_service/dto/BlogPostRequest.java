package com.devwonder.blog_service.dto;

import com.devwonder.blog_service.entity.BlogPost;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BlogPostRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title cannot exceed 500 characters")
    private String title;
    
    @NotBlank(message = "Slug is required")
    @Size(min = 3, max = 500, message = "Slug must be between 3 and 500 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug can only contain lowercase letters, numbers, and hyphens")
    private String slug;
    
    @Size(max = 1000, message = "Excerpt cannot exceed 1000 characters")
    private String excerpt;
    
    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters long")
    private String content;
    
    @Size(max = 500, message = "Featured image URL cannot exceed 500 characters")
    @Pattern(regexp = "^(https?://.*\\.(jpg|jpeg|png|gif|webp))?$", 
             message = "Featured image must be empty or a valid HTTP/HTTPS URL ending with jpg, jpeg, png, gif, or webp")
    private String featuredImage;
    
    @Size(max = 500, message = "Meta title cannot exceed 500 characters")
    private String metaTitle;
    
    @Size(max = 1000, message = "Meta description cannot exceed 1000 characters")
    private String metaDescription;
    
    @Size(max = 1000, message = "Meta keywords cannot exceed 1000 characters")
    private String metaKeywords;
    
    private BlogPost.PostStatus status = BlogPost.PostStatus.DRAFT;
    
    private Boolean isFeatured = false;
    
    @Min(value = 0, message = "Reading time cannot be negative")
    private Integer readingTime = 0;
    
    private LocalDateTime publishedAt;
    
    @NotNull(message = "Author ID is required")
    @Positive(message = "Author ID must be positive")
    private Long authorId;
    
    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;
    
    private Set<Long> tagIds;
}