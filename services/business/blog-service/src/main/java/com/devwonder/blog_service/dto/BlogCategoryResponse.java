package com.devwonder.blog_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlogCategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String color;
    private String icon;
    private Integer postsCount;
    private Boolean isVisible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}