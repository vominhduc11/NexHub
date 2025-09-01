package com.devwonder.blog_service.service;

import com.devwonder.blog_service.dto.BlogPostRequest;
import com.devwonder.blog_service.dto.BlogPostResponse;
import com.devwonder.blog_service.entity.BlogAuthor;
import com.devwonder.blog_service.entity.BlogCategory;
import com.devwonder.blog_service.entity.BlogPost;
import com.devwonder.blog_service.entity.BlogTag;
import com.devwonder.blog_service.mapper.BlogMapper;
import com.devwonder.blog_service.repository.BlogAuthorRepository;
import com.devwonder.blog_service.repository.BlogCategoryRepository;
import com.devwonder.blog_service.repository.BlogPostRepository;
import com.devwonder.blog_service.repository.BlogTagRepository;
import com.devwonder.blog_service.exception.BlogPostNotFoundException;
import com.devwonder.common.exception.ValidationException;
import com.devwonder.common.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BlogPostService {
    
    private final BlogPostRepository postRepository;
    private final BlogCategoryRepository categoryRepository;
    private final BlogAuthorRepository authorRepository;
    private final BlogTagRepository tagRepository;
    private final BlogMapper blogMapper;
    private final BlogPostCacheService cacheService;
    private final BlogPostStatsService statsService;
    
    // Get all published posts
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getAllPublishedPosts(int page, int size) {
        log.info("Fetching published posts - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = postRepository.findPublishedPosts(pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Get posts by category
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getPostsByCategory(Long categoryId, int page, int size) {
        log.info("Fetching posts by category: {} - page: {}, size: {}", categoryId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = postRepository.findPublishedPostsByCategory(categoryId, pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Get posts by author
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getPostsByAuthor(Long authorId, int page, int size) {
        log.info("Fetching posts by author: {} - page: {}, size: {}", authorId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = postRepository.findPublishedPostsByAuthor(authorId, pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Get featured posts
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getFeaturedPosts(int page, int size) {
        log.info("Fetching featured posts - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = postRepository.findFeaturedPosts(pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Get popular posts
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getPopularPosts(int page, int size) {
        log.info("Fetching popular posts - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = postRepository.findPopularPosts(pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Search posts
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> searchPosts(String keyword, int page, int size) {
        log.info("Searching posts with keyword: '{}' - page: {}, size: {}", keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = postRepository.searchPublishedPosts(keyword, pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Get posts by tag
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getPostsByTag(String tagSlug, int page, int size) {
        log.info("Fetching posts by tag: {} - page: {}, size: {}", tagSlug, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = postRepository.findPublishedPostsByTag(tagSlug, pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Get post by slug
    @Transactional(readOnly = true)
    public Optional<BlogPostResponse> getPostBySlug(String slug) {
        log.info("Fetching post by slug: {}", slug);
        
        Optional<BlogPost> post = postRepository.findPublishedBySlug(slug);
        
        // Increment view count if post found
        if (post.isPresent()) {
            statsService.incrementViewCount(post.get().getId());
        }
        
        return post.map(blogMapper::toPostResponse);
    }
    
    // Get related posts
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getRelatedPosts(Long postId, int limit) {
        log.info("Fetching related posts for post ID: {}, limit: {}", postId, limit);
        
        BlogPost currentPost = postRepository.findById(postId)
            .orElseThrow(() -> new BlogPostNotFoundException(postId));
        
        Pageable pageable = PageRequest.of(0, limit);
        Page<BlogPost> posts = postRepository.findRelatedPosts(currentPost.getCategory().getId(), postId, pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Create new post
    public BlogPostResponse createPost(BlogPostRequest request) {
        log.info("Creating new blog post: {}", request.getTitle());
        
        // Validate author exists
        BlogAuthor author = authorRepository.findById(request.getAuthorId())
            .orElseThrow(() -> new ValidationException("Author not found with ID: " + request.getAuthorId()));
        
        // Validate category exists
        BlogCategory category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ValidationException("Category not found with ID: " + request.getCategoryId()));
        
        // Check if slug already exists
        if (postRepository.existsBySlug(request.getSlug())) {
            throw new ValidationException("Post with slug '" + request.getSlug() + "' already exists");
        }
        
        // Create post entity
        BlogPost post = blogMapper.toPostEntity(request);
        post.setAuthor(author);
        post.setCategory(category);
        
        // Set tags if provided
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<BlogTag> tags = tagRepository.findByIdIn(request.getTagIds());
            post.setTags(tags);
        }
        
        // Set published date if status is PUBLISHED
        if (request.getStatus() == BlogPost.PostStatus.PUBLISHED && request.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
        
        BlogPost savedPost = postRepository.save(post);
        // LoggingUtils.logEntityCreation(log, "BlogPost", savedPost.getId());
        
        // Update category and author post counts
        statsService.updateRelatedCounts(savedPost);
        
        // Evict caches
        cacheService.evictPostListCaches();
        
        return blogMapper.toPostResponse(savedPost);
    }
    
    // Update post
    public BlogPostResponse updatePost(Long id, BlogPostRequest request) {
        log.info("Updating blog post with ID: {}", id);
        
        BlogPost post = postRepository.findById(id)
            .orElseThrow(() -> new BlogPostNotFoundException(id));
        
        validateUpdateRequest(post, request);
        updatePostFields(post, request);
        
        BlogPost updatedPost = postRepository.save(post);
        // LoggingUtils.logEntityUpdate(log, "BlogPost", updatedPost.getId());
        
        statsService.updateRelatedCounts(updatedPost);
        
        // Evict caches
        cacheService.evictAllPostCaches();
        
        return blogMapper.toPostResponse(updatedPost);
    }
    
    private void validateUpdateRequest(BlogPost post, BlogPostRequest request) {
        // Validate author exists
        authorRepository.findById(request.getAuthorId())
            .orElseThrow(() -> new ValidationException("Author not found with ID: " + request.getAuthorId()));
        
        // Validate category exists
        categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ValidationException("Category not found with ID: " + request.getCategoryId()));
        
        // Check if slug already exists (excluding current post)
        if (!post.getSlug().equals(request.getSlug()) && postRepository.existsBySlug(request.getSlug())) {
            throw new ValidationException("Post with slug '" + request.getSlug() + "' already exists");
        }
    }
    
    private void updatePostFields(BlogPost post, BlogPostRequest request) {
        // Get validated entities
        BlogAuthor author = authorRepository.findById(request.getAuthorId()).orElseThrow();
        BlogCategory category = categoryRepository.findById(request.getCategoryId()).orElseThrow();
        
        // Update basic fields
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
        post.setAuthor(author);
        post.setCategory(category);
        
        // Update tags if provided
        updatePostTags(post, request.getTagIds());
        
        // Handle publishing logic
        handlePublishingLogic(post, request.getStatus());
    }
    
    private void updatePostTags(BlogPost post, Set<Long> tagIds) {
        if (tagIds != null) {
            Set<BlogTag> tags = tagRepository.findByIdIn(tagIds);
            post.setTags(tags);
        }
    }
    
    private void handlePublishingLogic(BlogPost post, BlogPost.PostStatus status) {
        // Set published date if status changed to PUBLISHED
        if (status == BlogPost.PostStatus.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
    }
    
    // Delete post
    public void deletePost(Long id) {
        log.info("Deleting blog post with ID: {}", id);
        
        BlogPost post = postRepository.findById(id)
            .orElseThrow(() -> new BlogPostNotFoundException(id));
        
        postRepository.delete(post);
        log.info("Blog post deleted successfully: {}", post.getTitle());
        
        // Update counts
        statsService.updateRelatedCounts(post);
        
        // Evict caches
        cacheService.evictAllPostCaches();
    }
    
    // Publish post
    public BlogPostResponse publishPost(Long id) {
        log.info("Publishing blog post with ID: {}", id);
        
        BlogPost post = postRepository.findById(id)
            .orElseThrow(() -> new BlogPostNotFoundException(id));
        
        post.setStatus(BlogPost.PostStatus.PUBLISHED);
        if (post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
        
        BlogPost publishedPost = postRepository.save(post);
        log.info("Blog post published successfully: {}", publishedPost.getTitle());
        
        // Update counts
        statsService.updateRelatedCounts(publishedPost);
        
        // Evict caches
        cacheService.evictAllPostCaches();
        
        return blogMapper.toPostResponse(publishedPost);
    }
    
    // Like post
    public void likePost(Long id) {
        log.info("Liking blog post with ID: {}", id);
        
        statsService.incrementLikeCount(id);
    }
    
}