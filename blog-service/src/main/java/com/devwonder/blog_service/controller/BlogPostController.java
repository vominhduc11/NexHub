package com.devwonder.blog_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtils;
import com.devwonder.blog_service.dto.BlogPostRequest;
import com.devwonder.blog_service.dto.BlogPostResponse;
import com.devwonder.blog_service.service.BlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/blog/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Blog Posts", description = "APIs for blog post management")
public class BlogPostController {
    
    private final BlogPostService blogPostService;
    
    @GetMapping
    @Operation(summary = "Get all published blog posts", description = "Retrieve paginated list of published blog posts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getAllPosts(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts - page: {}, size: {}", page, size);
        
        try {
            Page<BlogPostResponse> posts = blogPostService.getAllPublishedPosts(page, size);
            return ResponseEntity.ok(BaseResponse.success(posts, "Posts retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving posts", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get posts by category", description = "Retrieve paginated list of posts in a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getPostsByCategory(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long categoryId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/category/{} - page: {}, size: {}", categoryId, page, size);
        
        try {
            Page<BlogPostResponse> posts = blogPostService.getPostsByCategory(categoryId, page, size);
            return ResponseEntity.ok(BaseResponse.success(posts, "Posts retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving posts by category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving posts", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get posts by author", description = "Retrieve paginated list of posts by a specific author")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getPostsByAuthor(
            @Parameter(description = "Author ID", example = "1") @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/author/{} - page: {}, size: {}", authorId, page, size);
        
        try {
            Page<BlogPostResponse> posts = blogPostService.getPostsByAuthor(authorId, page, size);
            return ResponseEntity.ok(BaseResponse.success(posts, "Posts retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving posts by author", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving posts", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/featured")
    @Operation(summary = "Get featured posts", description = "Retrieve paginated list of featured blog posts")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getFeaturedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/featured - page: {}, size: {}", page, size);
        
        try {
            Page<BlogPostResponse> posts = blogPostService.getFeaturedPosts(page, size);
            return ResponseUtils.success(posts, "Featured posts retrieved successfully");
        } catch (Exception e) {
            log.error("Error retrieving featured posts", e);
            return ResponseUtils.internalError("Error retrieving featured posts");
        }
    }
    
    @GetMapping("/popular")
    @Operation(summary = "Get popular posts", description = "Retrieve paginated list of popular blog posts ordered by views")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getPopularPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/popular - page: {}, size: {}", page, size);
        
        try {
            Page<BlogPostResponse> posts = blogPostService.getPopularPosts(page, size);
            return ResponseUtils.success(posts, "Popular posts retrieved successfully");
        } catch (Exception e) {
            log.error("Error retrieving popular posts", e);
            return ResponseUtils.internalError("Error retrieving popular posts");
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search blog posts", description = "Search published blog posts by keyword")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> searchPosts(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/search - keyword: '{}', page: {}, size: {}", keyword, page, size);
        
        try {
            Page<BlogPostResponse> posts = blogPostService.searchPosts(keyword, page, size);
            return ResponseEntity.ok(BaseResponse.success(posts, "Search results retrieved successfully"));
        } catch (Exception e) {
            log.error("Error searching posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error searching posts", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/tag/{tagSlug}")
    @Operation(summary = "Get posts by tag", description = "Retrieve paginated list of posts with a specific tag")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getPostsByTag(
            @Parameter(description = "Tag slug", example = "technology") @PathVariable String tagSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/tag/{} - page: {}, size: {}", tagSlug, page, size);
        
        try {
            Page<BlogPostResponse> posts = blogPostService.getPostsByTag(tagSlug, page, size);
            return ResponseEntity.ok(BaseResponse.success(posts, "Posts retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving posts by tag", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving posts", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get post by slug", description = "Retrieve a specific blog post by its slug")
    public ResponseEntity<BaseResponse<BlogPostResponse>> getPostBySlug(
            @Parameter(description = "Post slug", example = "my-first-blog-post") @PathVariable String slug) {
        
        log.info("GET /blog/posts/slug/{}", slug);
        
        try {
            Optional<BlogPostResponse> post = blogPostService.getPostBySlug(slug);
            if (post.isPresent()) {
                return ResponseEntity.ok(BaseResponse.success(post.get(), "Post retrieved successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Post not found", "NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("Error retrieving post by slug", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving post", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/{id}/related")
    @Operation(summary = "Get related posts", description = "Retrieve posts related to a specific post")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getRelatedPosts(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Limit", example = "5") @RequestParam(defaultValue = "5") int limit) {
        
        log.info("GET /blog/posts/{}/related - limit: {}", id, limit);
        
        try {
            Page<BlogPostResponse> posts = blogPostService.getRelatedPosts(id, limit);
            return ResponseEntity.ok(BaseResponse.success(posts, "Related posts retrieved successfully"));
        } catch (BaseException e) {
            log.error("Error retrieving related posts: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getHttpStatus())
                .body(BaseResponse.error(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Error retrieving related posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving related posts", "INTERNAL_ERROR"));
        }
    }
    
    @PostMapping
    @Operation(summary = "Create new blog post", description = "Create a new blog post. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<BlogPostResponse>> createPost(
            @Valid @RequestBody BlogPostRequest request) {
        
        log.info("POST /blog/posts - Creating post: {}", request.getTitle());
        
        try {
            BlogPostResponse response = blogPostService.createPost(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(response, "Post created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error creating post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error creating post", "INTERNAL_ERROR"));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update blog post", description = "Update an existing blog post. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<BlogPostResponse>> updatePost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody BlogPostRequest request) {
        
        log.info("PUT /blog/posts/{} - Updating post", id);
        
        try {
            BlogPostResponse response = blogPostService.updatePost(id, request);
            return ResponseEntity.ok(BaseResponse.success(response, "Post updated successfully"));
        } catch (BaseException e) {
            log.error("Error retrieving related posts: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getHttpStatus())
                .body(BaseResponse.error(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Error updating post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error updating post", "INTERNAL_ERROR"));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete blog post", description = "Delete a blog post. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<Void>> deletePost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id) {
        
        log.info("DELETE /blog/posts/{}", id);
        
        try {
            blogPostService.deletePost(id);
            return ResponseEntity.ok(BaseResponse.success("Post deleted successfully"));
        } catch (BaseException e) {
            log.error("Error retrieving related posts: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getHttpStatus())
                .body(BaseResponse.error(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Error deleting post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error deleting post", "INTERNAL_ERROR"));
        }
    }
    
    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish blog post", description = "Publish a draft blog post. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<BlogPostResponse>> publishPost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id) {
        
        log.info("POST /blog/posts/{}/publish", id);
        
        try {
            BlogPostResponse response = blogPostService.publishPost(id);
            return ResponseEntity.ok(BaseResponse.success(response, "Post published successfully"));
        } catch (BaseException e) {
            log.error("Error retrieving related posts: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getHttpStatus())
                .body(BaseResponse.error(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Error publishing post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error publishing post", "INTERNAL_ERROR"));
        }
    }
    
    @PostMapping("/{id}/like")
    @Operation(summary = "Like blog post", description = "Increment the like count of a blog post")
    public ResponseEntity<BaseResponse<Void>> likePost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id) {
        
        log.info("POST /blog/posts/{}/like", id);
        
        try {
            blogPostService.likePost(id);
            return ResponseEntity.ok(BaseResponse.success("Post liked successfully"));
        } catch (BaseException e) {
            log.error("Error retrieving related posts: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getHttpStatus())
                .body(BaseResponse.error(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Error liking post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error liking post", "INTERNAL_ERROR"));
        }
    }
}