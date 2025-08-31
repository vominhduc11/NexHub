package com.devwonder.blog_service.service;

import com.devwonder.blog_service.entity.BlogPost;
import com.devwonder.blog_service.repository.BlogAuthorRepository;
import com.devwonder.blog_service.repository.BlogCategoryRepository;
import com.devwonder.blog_service.repository.BlogPostRepository;
import com.devwonder.blog_service.repository.BlogTagRepository;
import com.devwonder.blog_service.exception.BlogPostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BlogPostStatsService {
    
    private final BlogPostRepository postRepository;
    private final BlogCategoryRepository categoryRepository;
    private final BlogAuthorRepository authorRepository;
    private final BlogTagRepository tagRepository;
    
    public void incrementViewCount(Long postId) {
        log.debug("Incrementing view count for post ID: {}", postId);
        postRepository.incrementViewsCount(postId);
    }
    
    public void incrementLikeCount(Long postId) {
        log.info("Incrementing like count for post ID: {}", postId);
        
        if (!postRepository.existsById(postId)) {
            throw new BlogPostNotFoundException(postId);
        }
        
        postRepository.incrementLikesCount(postId);
        log.info("Blog post like count incremented successfully: {}", postId);
    }
    
    public void updateRelatedCounts(BlogPost post) {
        log.debug("Updating related counts for post: {}", post.getTitle());
        
        categoryRepository.updatePostsCount(post.getCategory().getId());
        authorRepository.updateArticlesCount(post.getAuthor().getId());
        
        if (post.getTags() != null) {
            post.getTags().forEach(tag -> tagRepository.updatePostsCount(tag.getId()));
        }
    }
}