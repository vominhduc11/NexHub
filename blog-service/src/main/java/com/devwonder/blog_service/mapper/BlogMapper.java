package com.devwonder.blog_service.mapper;

import com.devwonder.blog_service.dto.*;
import com.devwonder.blog_service.entity.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BlogMapper {
    
    public BlogPostResponse toPostResponse(BlogPost post) {
        if (post == null) return null;
        
        BlogPostResponse response = new BlogPostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setSlug(post.getSlug());
        response.setExcerpt(post.getExcerpt());
        response.setContent(post.getContent());
        response.setFeaturedImage(post.getFeaturedImage());
        response.setMetaTitle(post.getMetaTitle());
        response.setMetaDescription(post.getMetaDescription());
        response.setMetaKeywords(post.getMetaKeywords());
        response.setStatus(post.getStatus());
        response.setIsFeatured(post.getIsFeatured());
        response.setViewsCount(post.getViewsCount());
        response.setLikesCount(post.getLikesCount());
        response.setCommentsCount(post.getCommentsCount());
        response.setReadingTime(post.getReadingTime());
        response.setPublishedAt(post.getPublishedAt());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        
        // Map author
        if (post.getAuthor() != null) {
            BlogPostResponse.AuthorSummary authorSummary = new BlogPostResponse.AuthorSummary();
            authorSummary.setId(post.getAuthor().getId());
            authorSummary.setName(post.getAuthor().getName());
            authorSummary.setTitle(post.getAuthor().getTitle());
            authorSummary.setAvatar(post.getAuthor().getAvatar());
            response.setAuthor(authorSummary);
        }
        
        // Map category
        if (post.getCategory() != null) {
            BlogPostResponse.CategorySummary categorySummary = new BlogPostResponse.CategorySummary();
            categorySummary.setId(post.getCategory().getId());
            categorySummary.setName(post.getCategory().getName());
            categorySummary.setSlug(post.getCategory().getSlug());
            categorySummary.setColor(post.getCategory().getColor());
            categorySummary.setIcon(post.getCategory().getIcon());
            response.setCategory(categorySummary);
        }
        
        // Map tags
        if (post.getTags() != null) {
            Set<BlogPostResponse.TagSummary> tagSummaries = post.getTags().stream()
                .map(tag -> {
                    BlogPostResponse.TagSummary tagSummary = new BlogPostResponse.TagSummary();
                    tagSummary.setId(tag.getId());
                    tagSummary.setName(tag.getName());
                    tagSummary.setSlug(tag.getSlug());
                    return tagSummary;
                })
                .collect(Collectors.toSet());
            response.setTags(tagSummaries);
        }
        
        return response;
    }
    
    public BlogCategoryResponse toCategoryResponse(BlogCategory category) {
        if (category == null) return null;
        
        BlogCategoryResponse response = new BlogCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setDescription(category.getDescription());
        response.setColor(category.getColor());
        response.setIcon(category.getIcon());
        response.setPostsCount(category.getPostsCount());
        response.setIsVisible(category.getIsVisible());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        
        return response;
    }
    
    public CommentResponse toCommentResponse(BlogComment comment) {
        if (comment == null) return null;
        
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setAuthorName(comment.getAuthorName());
        response.setAuthorEmail(comment.getAuthorEmail());
        response.setAuthorAvatar(comment.getAuthorAvatar());
        response.setContent(comment.getContent());
        response.setIsApproved(comment.getIsApproved());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        response.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
        
        // Map replies
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            response.setReplies(comment.getReplies().stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList()));
        }
        
        return response;
    }
    
    public BlogPost toPostEntity(BlogPostRequest request) {
        if (request == null) return null;
        
        BlogPost post = new BlogPost();
        post.setTitle(request.getTitle());
        post.setSlug(request.getSlug());
        post.setExcerpt(request.getExcerpt());
        post.setContent(request.getContent());
        post.setFeaturedImage(request.getFeaturedImage());
        post.setMetaTitle(request.getMetaTitle());
        post.setMetaDescription(request.getMetaDescription());
        post.setMetaKeywords(request.getMetaKeywords());
        post.setStatus(request.getStatus());
        post.setIsFeatured(request.getIsFeatured());
        post.setReadingTime(request.getReadingTime());
        post.setPublishedAt(request.getPublishedAt());
        
        return post;
    }
    
    public BlogCategory toCategoryEntity(BlogCategoryRequest request) {
        if (request == null) return null;
        
        BlogCategory category = new BlogCategory();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        category.setIsVisible(request.getIsVisible());
        
        return category;
    }
    
    public BlogComment toCommentEntity(CommentRequest request, BlogPost post, BlogComment parentComment) {
        if (request == null) return null;
        
        BlogComment comment = new BlogComment();
        comment.setPost(post);
        comment.setParentComment(parentComment);
        comment.setAuthorName(request.getAuthorName());
        comment.setAuthorEmail(request.getAuthorEmail());
        comment.setContent(request.getContent());
        comment.setIsApproved(false); // Comments need approval by default
        
        return comment;
    }
}