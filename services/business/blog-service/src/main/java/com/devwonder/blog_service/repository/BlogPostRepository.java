package com.devwonder.blog_service.repository;

import com.devwonder.blog_service.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    // Find published posts
    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' AND p.deletedAt IS NULL ORDER BY p.publishedAt DESC")
    Page<BlogPost> findPublishedPosts(Pageable pageable);
    
    // Find posts by category
    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' AND p.category.id = :categoryId AND p.deletedAt IS NULL ORDER BY p.publishedAt DESC")
    Page<BlogPost> findPublishedPostsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // Find posts by author
    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' AND p.author.id = :authorId AND p.deletedAt IS NULL ORDER BY p.publishedAt DESC")
    Page<BlogPost> findPublishedPostsByAuthor(@Param("authorId") Long authorId, Pageable pageable);
    
    // Find featured posts
    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' AND p.isFeatured = true AND p.deletedAt IS NULL ORDER BY p.publishedAt DESC")
    Page<BlogPost> findFeaturedPosts(Pageable pageable);
    
    // Find popular posts (by views)
    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' AND p.deletedAt IS NULL ORDER BY p.viewsCount DESC, p.publishedAt DESC")
    Page<BlogPost> findPopularPosts(Pageable pageable);
    
    // Search posts
    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' AND p.deletedAt IS NULL AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.publishedAt DESC")
    Page<BlogPost> searchPublishedPosts(@Param("keyword") String keyword, Pageable pageable);
    
    // Find by slug
    @Query("SELECT p FROM BlogPost p WHERE p.slug = :slug AND p.status = 'PUBLISHED' AND p.deletedAt IS NULL")
    Optional<BlogPost> findPublishedBySlug(@Param("slug") String slug);
    
    // Find posts by tag
    @Query("SELECT DISTINCT p FROM BlogPost p JOIN p.tags t WHERE p.status = 'PUBLISHED' AND t.slug = :tagSlug AND p.deletedAt IS NULL ORDER BY p.publishedAt DESC")
    Page<BlogPost> findPublishedPostsByTag(@Param("tagSlug") String tagSlug, Pageable pageable);
    
    // Find related posts (same category, excluding current post)
    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' AND p.category.id = :categoryId AND p.id != :excludeId AND p.deletedAt IS NULL ORDER BY p.publishedAt DESC")
    Page<BlogPost> findRelatedPosts(@Param("categoryId") Long categoryId, @Param("excludeId") Long excludeId, Pageable pageable);
    
    // Check if slug exists
    boolean existsBySlug(String slug);
    
    // Increment views count
    @Modifying
    @Query("UPDATE BlogPost p SET p.viewsCount = p.viewsCount + 1 WHERE p.id = :postId")
    void incrementViewsCount(@Param("postId") Long postId);
    
    // Increment likes count
    @Modifying
    @Query("UPDATE BlogPost p SET p.likesCount = p.likesCount + 1 WHERE p.id = :postId")
    void incrementLikesCount(@Param("postId") Long postId);
    
    // Update comments count
    @Modifying
    @Query("UPDATE BlogPost p SET p.commentsCount = (SELECT COUNT(c) FROM BlogComment c WHERE c.post.id = p.id AND c.isApproved = true) WHERE p.id = :postId")
    void updateCommentsCount(@Param("postId") Long postId);
    
    // Soft delete methods
    @Modifying
    @Query("UPDATE BlogPost p SET p.deletedAt = CURRENT_TIMESTAMP WHERE p.id = :postId")
    void softDelete(@Param("postId") Long postId);
    
    @Modifying
    @Query("UPDATE BlogPost p SET p.deletedAt = NULL WHERE p.id = :postId")
    void restore(@Param("postId") Long postId);
    
    @Query("SELECT p FROM BlogPost p WHERE p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    Page<BlogPost> findAllActive(Pageable pageable);
}