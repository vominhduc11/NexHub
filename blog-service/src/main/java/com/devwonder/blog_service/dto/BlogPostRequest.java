package com.devwonder.blog_service.dto;

import com.devwonder.blog_service.entity.BlogPost;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Size(max = 500, message = "Slug cannot exceed 500 characters")
    private String slug;
    
    @Size(max = 1000, message = "Excerpt cannot exceed 1000 characters")
    private String excerpt;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String featuredImage;
    
    @Size(max = 500, message = "Meta title cannot exceed 500 characters")
    private String metaTitle;
    
    @Size(max = 1000, message = "Meta description cannot exceed 1000 characters")
    private String metaDescription;
    
    @Size(max = 1000, message = "Meta keywords cannot exceed 1000 characters")
    private String metaKeywords;
    
    private BlogPost.PostStatus status = BlogPost.PostStatus.DRAFT;
    
    private Boolean isFeatured = false;
    
    private Integer readingTime = 0;
    
    private LocalDateTime publishedAt;
    
    @NotNull(message = "Author ID is required")
    private Long authorId;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private Set<Long> tagIds;
}