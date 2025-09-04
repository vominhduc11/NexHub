package com.devwonder.blog_service.dto;

import com.devwonder.blog_service.entity.BlogPost;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BlogPostResponse {
    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private String featuredImage;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private BlogPost.PostStatus status;
    private Boolean isFeatured;
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer readingTime;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Author info
    private AuthorSummary author;
    
    // Category info
    private CategorySummary category;
    
    // Tags
    private Set<TagSummary> tags;
    
    @Data
    public static class AuthorSummary {
        private Long id;
        private String name;
        private String title;
        private String avatar;
    }
    
    @Data
    public static class CategorySummary {
        private Long id;
        private String name;
        private String slug;
        private String color;
        private String icon;
    }
    
    @Data
    public static class TagSummary {
        private Long id;
        private String name;
        private String slug;
    }
}