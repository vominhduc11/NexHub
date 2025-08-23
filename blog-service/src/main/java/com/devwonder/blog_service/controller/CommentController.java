package com.devwonder.blog_service.controller;

import com.devwonder.blog_service.dto.BaseResponse;
import com.devwonder.blog_service.dto.CommentRequest;
import com.devwonder.blog_service.dto.CommentResponse;
import com.devwonder.blog_service.service.CommentService;
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

import java.util.List;

@RestController
@RequestMapping("/blog/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Blog Comments", description = "APIs for blog comment management")
public class CommentController {
    
    private final CommentService commentService;
    
    @GetMapping("/post/{postId}")
    @Operation(summary = "Get approved comments by post", description = "Retrieve all approved comments for a specific post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<BaseResponse<List<CommentResponse>>> getCommentsByPost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long postId) {
        
        log.info("GET /blog/comments/post/{}", postId);
        
        try {
            List<CommentResponse> comments = commentService.getCommentsByPost(postId);
            return ResponseEntity.ok(BaseResponse.success(comments, "Comments retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving comments for post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving comments", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/post/{postId}/paginated")
    @Operation(summary = "Get approved comments by post with pagination", description = "Retrieve paginated approved comments for a specific post")
    public ResponseEntity<BaseResponse<Page<CommentResponse>>> getCommentsByPost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long postId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/comments/post/{}/paginated - page: {}, size: {}", postId, page, size);
        
        try {
            Page<CommentResponse> comments = commentService.getCommentsByPost(postId, page, size);
            return ResponseEntity.ok(BaseResponse.success(comments, "Comments retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving paginated comments for post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving comments", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/post/{postId}/all")
    @Operation(summary = "Get all comments by post", description = "Retrieve all comments (approved and pending) for a specific post. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<Page<CommentResponse>>> getAllCommentsByPost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/comments/post/{}/all - page: {}, size: {}", postId, page, size);
        
        try {
            Page<CommentResponse> comments = commentService.getAllCommentsByPost(postId, page, size);
            return ResponseEntity.ok(BaseResponse.success(comments, "All comments retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving all comments for post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving comments", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/pending")
    @Operation(summary = "Get pending comments", description = "Retrieve all comments waiting for approval. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<Page<CommentResponse>>> getPendingComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/comments/pending - page: {}, size: {}", page, size);
        
        try {
            Page<CommentResponse> comments = commentService.getPendingComments(page, size);
            return ResponseEntity.ok(BaseResponse.success(comments, "Pending comments retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving pending comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving pending comments", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Get recent comments", description = "Retrieve recently approved comments across all posts")
    public ResponseEntity<BaseResponse<Page<CommentResponse>>> getRecentComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/comments/recent - page: {}, size: {}", page, size);
        
        try {
            Page<CommentResponse> comments = commentService.getRecentComments(page, size);
            return ResponseEntity.ok(BaseResponse.success(comments, "Recent comments retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving recent comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving recent comments", "INTERNAL_ERROR"));
        }
    }
    
    @GetMapping("/post/{postId}/count")
    @Operation(summary = "Get comment count", description = "Get the number of approved comments for a specific post")
    public ResponseEntity<BaseResponse<Long>> getCommentCount(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long postId) {
        
        log.info("GET /blog/comments/post/{}/count", postId);
        
        try {
            Long count = commentService.getCommentCount(postId);
            return ResponseEntity.ok(BaseResponse.success(count, "Comment count retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving comment count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving comment count", "INTERNAL_ERROR"));
        }
    }
    
    @PostMapping("/post/{postId}")
    @Operation(summary = "Create new comment", description = "Create a new comment for a blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comment created successfully (pending approval)"),
        @ApiResponse(responseCode = "400", description = "Invalid comment data"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<BaseResponse<CommentResponse>> createComment(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request) {
        
        log.info("POST /blog/comments/post/{} - Creating comment by {}", postId, request.getAuthorName());
        
        try {
            CommentResponse response = commentService.createComment(postId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(response, "Comment created successfully and is pending approval"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error creating comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error creating comment", "INTERNAL_ERROR"));
        }
    }
    
    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve comment", description = "Approve a pending comment. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<CommentResponse>> approveComment(
            @Parameter(description = "Comment ID", example = "1") @PathVariable Long id) {
        
        log.info("PATCH /blog/comments/{}/approve", id);
        
        try {
            CommentResponse response = commentService.approveComment(id);
            return ResponseEntity.ok(BaseResponse.success(response, "Comment approved successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Comment not found", "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error approving comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error approving comment", "INTERNAL_ERROR"));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Delete a comment. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<Void>> deleteComment(
            @Parameter(description = "Comment ID", example = "1") @PathVariable Long id) {
        
        log.info("DELETE /blog/comments/{}", id);
        
        try {
            commentService.deleteComment(id);
            return ResponseEntity.ok(BaseResponse.success("Comment deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Comment not found", "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error deleting comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error deleting comment", "INTERNAL_ERROR"));
        }
    }
}