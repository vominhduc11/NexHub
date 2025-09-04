package com.devwonder.blog_service.repository;

import com.devwonder.blog_service.entity.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {
    
    // Find comments by post (only approved, top-level comments with replies)
    @Query("SELECT c FROM BlogComment c WHERE c.post.id = :postId AND c.parentComment IS NULL AND c.isApproved = true ORDER BY c.createdAt DESC")
    List<BlogComment> findApprovedCommentsByPost(@Param("postId") Long postId);
    
    @Query("SELECT c FROM BlogComment c WHERE c.post.id = :postId AND c.parentComment IS NULL AND c.isApproved = true ORDER BY c.createdAt DESC")
    Page<BlogComment> findApprovedCommentsByPost(@Param("postId") Long postId, Pageable pageable);
    
    // Find all comments by post (including unapproved - for admin)
    @Query("SELECT c FROM BlogComment c WHERE c.post.id = :postId AND c.parentComment IS NULL ORDER BY c.createdAt DESC")
    Page<BlogComment> findAllCommentsByPost(@Param("postId") Long postId, Pageable pageable);
    
    // Find replies to a comment
    @Query("SELECT c FROM BlogComment c WHERE c.parentComment.id = :parentId AND c.isApproved = true ORDER BY c.createdAt ASC")
    List<BlogComment> findApprovedRepliesByParent(@Param("parentId") Long parentId);
    
    // Find pending comments (for moderation)
    @Query("SELECT c FROM BlogComment c WHERE c.isApproved = false ORDER BY c.createdAt DESC")
    Page<BlogComment> findPendingComments(Pageable pageable);
    
    // Count approved comments by post
    @Query("SELECT COUNT(c) FROM BlogComment c WHERE c.post.id = :postId AND c.isApproved = true")
    Long countApprovedCommentsByPost(@Param("postId") Long postId);
    
    // Find recent comments
    @Query("SELECT c FROM BlogComment c WHERE c.isApproved = true ORDER BY c.createdAt DESC")
    Page<BlogComment> findRecentApprovedComments(Pageable pageable);
}