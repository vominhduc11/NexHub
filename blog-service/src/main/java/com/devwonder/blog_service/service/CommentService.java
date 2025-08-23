package com.devwonder.blog_service.service;

import com.devwonder.blog_service.dto.CommentRequest;
import com.devwonder.blog_service.dto.CommentResponse;
import com.devwonder.blog_service.entity.BlogComment;
import com.devwonder.blog_service.entity.BlogPost;
import com.devwonder.blog_service.mapper.BlogMapper;
import com.devwonder.blog_service.repository.BlogCommentRepository;
import com.devwonder.blog_service.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {
    
    private final BlogCommentRepository commentRepository;
    private final BlogPostRepository postRepository;
    private final BlogMapper blogMapper;
    
    // Get comments by post ID
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPost(Long postId) {
        log.info("Fetching approved comments for post ID: {}", postId);
        
        List<BlogComment> comments = commentRepository.findApprovedCommentsByPost(postId);
        return comments.stream()
            .map(blogMapper::toCommentResponse)
            .collect(Collectors.toList());
    }
    
    // Get comments by post ID with pagination
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByPost(Long postId, int page, int size) {
        log.info("Fetching approved comments for post ID: {} - page: {}, size: {}", postId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogComment> comments = commentRepository.findApprovedCommentsByPost(postId, pageable);
        
        return comments.map(blogMapper::toCommentResponse);
    }
    
    // Get all comments by post (for admin)
    @Transactional(readOnly = true)
    public Page<CommentResponse> getAllCommentsByPost(Long postId, int page, int size) {
        log.info("Fetching all comments for post ID: {} - page: {}, size: {}", postId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogComment> comments = commentRepository.findAllCommentsByPost(postId, pageable);
        
        return comments.map(blogMapper::toCommentResponse);
    }
    
    // Get pending comments (for moderation)
    @Transactional(readOnly = true)
    public Page<CommentResponse> getPendingComments(int page, int size) {
        log.info("Fetching pending comments - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogComment> comments = commentRepository.findPendingComments(pageable);
        
        return comments.map(blogMapper::toCommentResponse);
    }
    
    // Get recent comments
    @Transactional(readOnly = true)
    public Page<CommentResponse> getRecentComments(int page, int size) {
        log.info("Fetching recent approved comments - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogComment> comments = commentRepository.findRecentApprovedComments(pageable);
        
        return comments.map(blogMapper::toCommentResponse);
    }
    
    // Create new comment
    public CommentResponse createComment(Long postId, CommentRequest request) {
        log.info("Creating new comment for post ID: {} by {}", postId, request.getAuthorName());
        
        // Validate post exists
        BlogPost post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
        
        // Validate parent comment if provided
        BlogComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found with ID: " + request.getParentCommentId()));
            
            // Ensure parent comment belongs to the same post
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new IllegalArgumentException("Parent comment does not belong to the specified post");
            }
        }
        
        // Create comment entity
        BlogComment comment = blogMapper.toCommentEntity(request, post, parentComment);
        BlogComment savedComment = commentRepository.save(comment);
        
        log.info("Comment created successfully with ID: {} (pending approval)", savedComment.getId());
        return blogMapper.toCommentResponse(savedComment);
    }
    
    // Approve comment
    public CommentResponse approveComment(Long commentId) {
        log.info("Approving comment with ID: {}", commentId);
        
        BlogComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        comment.setIsApproved(true);
        BlogComment approvedComment = commentRepository.save(comment);
        
        // Update post comments count
        postRepository.updateCommentsCount(comment.getPost().getId());
        
        log.info("Comment approved successfully: {}", approvedComment.getId());
        return blogMapper.toCommentResponse(approvedComment);
    }
    
    // Reject/Delete comment
    public void deleteComment(Long commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        
        BlogComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        Long postId = comment.getPost().getId();
        commentRepository.delete(comment);
        
        // Update post comments count
        postRepository.updateCommentsCount(postId);
        
        log.info("Comment deleted successfully: {}", commentId);
    }
    
    // Get comment count for post
    @Transactional(readOnly = true)
    public Long getCommentCount(Long postId) {
        log.info("Getting comment count for post ID: {}", postId);
        
        return commentRepository.countApprovedCommentsByPost(postId);
    }
}