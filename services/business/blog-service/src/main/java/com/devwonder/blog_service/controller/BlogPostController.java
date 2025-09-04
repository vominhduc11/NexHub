package com.devwonder.blog_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
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
        
        Page<BlogPostResponse> posts = blogPostService.getAllPublishedPosts(page, size);
        return ResponseEntity.ok(BaseResponse.success("Posts retrieved successfully", posts));
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
        
        Page<BlogPostResponse> posts = blogPostService.getPostsByCategory(categoryId, page, size);
        return ResponseEntity.ok(BaseResponse.success("Posts retrieved successfully", posts));
    }
    
    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get posts by author", description = "Retrieve paginated list of posts by a specific author")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getPostsByAuthor(
            @Parameter(description = "Author ID", example = "1") @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/author/{} - page: {}, size: {}", authorId, page, size);
        
        Page<BlogPostResponse> posts = blogPostService.getPostsByAuthor(authorId, page, size);
        return ResponseEntity.ok(BaseResponse.success("Posts retrieved successfully", posts));
    }
    
    @GetMapping("/featured")
    @Operation(summary = "Get featured posts", description = "Retrieve paginated list of featured blog posts")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getFeaturedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/featured - page: {}, size: {}", page, size);
        
        Page<BlogPostResponse> posts = blogPostService.getFeaturedPosts(page, size);
        return ResponseUtil.success("Featured posts retrieved successfully", posts);
    }
    
    @GetMapping("/popular")
    @Operation(summary = "Get popular posts", description = "Retrieve paginated list of popular blog posts ordered by views")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getPopularPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/popular - page: {}, size: {}", page, size);
        
        Page<BlogPostResponse> posts = blogPostService.getPopularPosts(page, size);
        return ResponseUtil.success("Popular posts retrieved successfully", posts);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search blog posts", description = "Search published blog posts by keyword")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> searchPosts(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/search - keyword: '{}', page: {}, size: {}", keyword, page, size);
        
        Page<BlogPostResponse> posts = blogPostService.searchPosts(keyword, page, size);
        return ResponseEntity.ok(BaseResponse.success("Search results retrieved successfully", posts));
    }
    
    @GetMapping("/tag/{tagSlug}")
    @Operation(summary = "Get posts by tag", description = "Retrieve paginated list of posts with a specific tag")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getPostsByTag(
            @Parameter(description = "Tag slug", example = "technology") @PathVariable String tagSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/posts/tag/{} - page: {}, size: {}", tagSlug, page, size);
        
        Page<BlogPostResponse> posts = blogPostService.getPostsByTag(tagSlug, page, size);
        return ResponseEntity.ok(BaseResponse.success("Posts retrieved successfully", posts));
    }
    
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get post by slug", description = "Retrieve a specific blog post by its slug")
    public ResponseEntity<BaseResponse<BlogPostResponse>> getPostBySlug(
            @Parameter(description = "Post slug", example = "my-first-blog-post") @PathVariable String slug) {
        
        log.info("GET /blog/posts/slug/{}", slug);
        
        Optional<BlogPostResponse> post = blogPostService.getPostBySlug(slug);
        if (post.isPresent()) {
            return ResponseEntity.ok(BaseResponse.success("Post retrieved successfully", post.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error("Post not found", "NOT_FOUND"));
        }
    }
    
    @GetMapping("/{id}/related")
    @Operation(summary = "Get related posts", description = "Retrieve posts related to a specific post")
    public ResponseEntity<BaseResponse<Page<BlogPostResponse>>> getRelatedPosts(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Limit", example = "5") @RequestParam(defaultValue = "5") int limit) {
        
        log.info("GET /blog/posts/{}/related - limit: {}", id, limit);
        
        Page<BlogPostResponse> posts = blogPostService.getRelatedPosts(id, limit);
        return ResponseEntity.ok(BaseResponse.success("Related posts retrieved successfully", posts));
    }
    
    @PostMapping
    @Operation(summary = "Create new blog post", description = "Create a new blog post. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<BlogPostResponse>> createPost(
            @Valid @RequestBody BlogPostRequest request) {
        
        log.info("POST /blog/posts - Creating post: {}", request.getTitle());
        
        BlogPostResponse response = blogPostService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BaseResponse.success("Post created successfully", response));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update blog post", description = "Update an existing blog post. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<BlogPostResponse>> updatePost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody BlogPostRequest request) {
        
        log.info("PUT /blog/posts/{} - Updating post", id);
        
        BlogPostResponse response = blogPostService.updatePost(id, request);
        return ResponseEntity.ok(BaseResponse.success("Post updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete blog post", description = "Delete a blog post. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<Void>> deletePost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id) {
        
        log.info("DELETE /blog/posts/{}", id);
        
        blogPostService.deletePost(id);
        return ResponseEntity.ok(BaseResponse.success("Post deleted successfully"));
    }
    
    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish blog post", description = "Publish a draft blog post. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<BlogPostResponse>> publishPost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id) {
        
        log.info("POST /blog/posts/{}/publish", id);
        
        BlogPostResponse response = blogPostService.publishPost(id);
        return ResponseEntity.ok(BaseResponse.success("Post published successfully", response));
    }
    
    @PostMapping("/{id}/like")
    @Operation(summary = "Like blog post", description = "Increment the like count of a blog post")
    public ResponseEntity<BaseResponse<Void>> likePost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id) {
        
        log.info("POST /blog/posts/{}/like", id);
        
        blogPostService.likePost(id);
        return ResponseEntity.ok(BaseResponse.success("Post liked successfully"));
    }
}