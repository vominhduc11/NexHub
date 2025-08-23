package com.devwonder.blog_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentResponse {
    private Long id;
    private String authorName;
    private String authorEmail;
    private String authorAvatar;
    private String content;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentCommentId;
    private List<CommentResponse> replies;
}