package com.devwonder.blog_service.exception;

import com.devwonder.common.exception.ResourceNotFoundException;

public class BlogPostNotFoundException extends ResourceNotFoundException {
    public BlogPostNotFoundException(Long postId) {
        super("Blog post", "id", String.valueOf(String.valueOf(postId)));
    }

    public BlogPostNotFoundException(String fieldName, Object fieldValue) {
        super("Blog post", fieldName, String.valueOf(fieldValue));
    }
}