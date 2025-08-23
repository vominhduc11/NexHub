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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

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
    
    // Get all published posts
    @Transactional(readOnly = true)
    @Cacheable(value = "blog-posts", key = "'page:' + #page + ':size:' + #size")
    public Page<BlogPostResponse> getAllPublishedPosts(int page, int size) {
        log.info("Fetching published posts - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = postRepository.findPublishedPosts(pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Get posts by category
    @Transactional(readOnly = true)
    @Cacheable(value = "blog-posts-by-category", key = "'category:' + #categoryId + ':page:' + #page + ':size:' + #size")
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
    @Cacheable(value = "blog-posts-featured", key = "'page:' + #page + ':size:' + #size")
    public Page<BlogPostResponse> getFeaturedPosts(int page, int size) {
        log.info("Fetching featured posts - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = postRepository.findFeaturedPosts(pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Get popular posts
    @Transactional(readOnly = true)
    @Cacheable(value = "blog-posts-popular", key = "'page:' + #page + ':size:' + #size")
    public Page<BlogPostResponse> getPopularPosts(int page, int size) {
        log.info("Fetching popular posts - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = postRepository.findPopularPosts(pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Search posts
    @Transactional(readOnly = true)
    @Cacheable(value = "blog-posts-search", key = "'keyword:' + #keyword + ':page:' + #page + ':size:' + #size")
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
    @Cacheable(value = "blog-posts-by-slug", key = "'slug:' + #slug")
    public Optional<BlogPostResponse> getPostBySlug(String slug) {
        log.info("Fetching post by slug: {}", slug);
        
        Optional<BlogPost> post = postRepository.findPublishedBySlug(slug);
        
        // Increment view count if post found
        if (post.isPresent()) {
            postRepository.incrementViewsCount(post.get().getId());
        }
        
        return post.map(blogMapper::toPostResponse);
    }
    
    // Get related posts
    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getRelatedPosts(Long postId, int limit) {
        log.info("Fetching related posts for post ID: {}, limit: {}", postId, limit);
        
        BlogPost currentPost = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        
        Pageable pageable = PageRequest.of(0, limit);
        Page<BlogPost> posts = postRepository.findRelatedPosts(currentPost.getCategory().getId(), postId, pageable);
        
        return posts.map(blogMapper::toPostResponse);
    }
    
    // Create new post
    @CacheEvict(value = {"blog-posts", "blog-posts-by-category", "blog-posts-featured", "blog-posts-popular", "blog-posts-search"}, allEntries = true)
    public BlogPostResponse createPost(BlogPostRequest request) {
        log.info("Creating new blog post: {}", request.getTitle());
        
        // Validate author exists
        BlogAuthor author = authorRepository.findById(request.getAuthorId())
            .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + request.getAuthorId()));
        
        // Validate category exists
        BlogCategory category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + request.getCategoryId()));
        
        // Check if slug already exists
        if (postRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Post with slug '" + request.getSlug() + "' already exists");
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
        log.info("Blog post created successfully with ID: {}", savedPost.getId());
        
        // Update category and author post counts
        updateCounts(savedPost);
        
        return blogMapper.toPostResponse(savedPost);
    }
    
    // Update post
    @CacheEvict(value = {"blog-posts", "blog-posts-by-category", "blog-posts-featured", "blog-posts-popular", "blog-posts-search", "blog-posts-by-slug"}, allEntries = true)
    public BlogPostResponse updatePost(Long id, BlogPostRequest request) {
        log.info("Updating blog post with ID: {}", id);
        
        BlogPost post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        
        // Validate author exists
        BlogAuthor author = authorRepository.findById(request.getAuthorId())
            .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + request.getAuthorId()));
        
        // Validate category exists
        BlogCategory category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + request.getCategoryId()));
        
        // Check if slug already exists (excluding current post)
        if (!post.getSlug().equals(request.getSlug()) && postRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Post with slug '" + request.getSlug() + "' already exists");
        }
        
        // Update post fields
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
        if (request.getTagIds() != null) {
            Set<BlogTag> tags = tagRepository.findByIdIn(request.getTagIds());
            post.setTags(tags);
        }
        
        // Set published date if status changed to PUBLISHED
        if (request.getStatus() == BlogPost.PostStatus.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
        
        BlogPost updatedPost = postRepository.save(post);
        log.info("Blog post updated successfully: {}", updatedPost.getTitle());
        
        // Update counts
        updateCounts(updatedPost);
        
        return blogMapper.toPostResponse(updatedPost);
    }
    
    // Delete post
    @CacheEvict(value = {"blog-posts", "blog-posts-by-category", "blog-posts-featured", "blog-posts-popular", "blog-posts-search", "blog-posts-by-slug"}, allEntries = true)
    public void deletePost(Long id) {
        log.info("Deleting blog post with ID: {}", id);
        
        BlogPost post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        
        postRepository.delete(post);
        log.info("Blog post deleted successfully: {}", post.getTitle());
        
        // Update counts
        updateCounts(post);
    }
    
    // Publish post
    @CacheEvict(value = {"blog-posts", "blog-posts-by-category", "blog-posts-featured", "blog-posts-popular", "blog-posts-search", "blog-posts-by-slug"}, allEntries = true)
    public BlogPostResponse publishPost(Long id) {
        log.info("Publishing blog post with ID: {}", id);
        
        BlogPost post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        
        post.setStatus(BlogPost.PostStatus.PUBLISHED);
        if (post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
        
        BlogPost publishedPost = postRepository.save(post);
        log.info("Blog post published successfully: {}", publishedPost.getTitle());
        
        // Update counts
        updateCounts(publishedPost);
        
        return blogMapper.toPostResponse(publishedPost);
    }
    
    // Like post
    public void likePost(Long id) {
        log.info("Liking blog post with ID: {}", id);
        
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        
        postRepository.incrementLikesCount(id);
        log.info("Blog post liked successfully: {}", id);
    }
    
    private void updateCounts(BlogPost post) {
        // Update category posts count
        categoryRepository.updatePostsCount(post.getCategory().getId());
        
        // Update author articles count
        authorRepository.updateArticlesCount(post.getAuthor().getId());
        
        // Update tags posts count
        if (post.getTags() != null) {
            post.getTags().forEach(tag -> tagRepository.updatePostsCount(tag.getId()));
        }
    }
}