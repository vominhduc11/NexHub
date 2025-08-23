package com.devwonder.blog_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    
    @NotBlank(message = "Author name is required")
    @Size(max = 255, message = "Author name cannot exceed 255 characters")
    private String authorName;
    
    @NotBlank(message = "Author email is required")
    @Email(message = "Invalid email format")
    private String authorEmail;
    
    private String authorWebsite;
    
    @NotBlank(message = "Comment content is required")
    @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
    private String content;
    
    private Long parentCommentId;
}