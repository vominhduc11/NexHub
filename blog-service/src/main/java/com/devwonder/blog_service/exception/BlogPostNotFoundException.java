package com.devwonder.blog_service.exception;

import com.devwonder.common.exception.ResourceNotFoundException;

public class BlogPostNotFoundException extends ResourceNotFoundException {
    public BlogPostNotFoundException(Long postId) {
        super("Blog post", "id", postId);
    }

    public BlogPostNotFoundException(String fieldName, Object fieldValue) {
        super("Blog post", fieldName, fieldValue);
    }
}