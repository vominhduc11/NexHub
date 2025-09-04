package com.devwonder.blog_service.repository;

import com.devwonder.blog_service.entity.BlogCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {
    
    // Find visible categories
    @Query("SELECT c FROM BlogCategory c WHERE c.isVisible = true AND c.deletedAt IS NULL ORDER BY c.postsCount DESC, c.name ASC")
    List<BlogCategory> findVisibleCategories();
    
    @Query("SELECT c FROM BlogCategory c WHERE c.isVisible = true AND c.deletedAt IS NULL ORDER BY c.postsCount DESC, c.name ASC")
    Page<BlogCategory> findVisibleCategories(Pageable pageable);
    
    // Find by slug
    Optional<BlogCategory> findBySlug(String slug);
    
    // Find visible by slug
    @Query("SELECT c FROM BlogCategory c WHERE c.slug = :slug AND c.isVisible = true AND c.deletedAt IS NULL")
    Optional<BlogCategory> findVisibleBySlug(@Param("slug") String slug);
    
    // Check if slug exists
    boolean existsBySlug(String slug);
    
    // Update posts count
    @Modifying
    @Query("UPDATE BlogCategory c SET c.postsCount = (SELECT COUNT(p) FROM BlogPost p WHERE p.category.id = c.id AND p.status = 'PUBLISHED' AND p.deletedAt IS NULL) WHERE c.id = :categoryId")
    void updatePostsCount(@Param("categoryId") Long categoryId);
    
    // Update posts count for all categories
    @Modifying
    @Query("UPDATE BlogCategory c SET c.postsCount = (SELECT COUNT(p) FROM BlogPost p WHERE p.category.id = c.id AND p.status = 'PUBLISHED' AND p.deletedAt IS NULL)")
    void updateAllPostsCounts();
    
    // Soft delete methods
    @Modifying
    @Query("UPDATE BlogCategory c SET c.deletedAt = CURRENT_TIMESTAMP WHERE c.id = :categoryId")
    void softDelete(@Param("categoryId") Long categoryId);
    
    @Modifying
    @Query("UPDATE BlogCategory c SET c.deletedAt = NULL WHERE c.id = :categoryId")
    void restore(@Param("categoryId") Long categoryId);
    
    @Query("SELECT c FROM BlogCategory c WHERE c.deletedAt IS NULL ORDER BY c.postsCount DESC, c.name ASC")
    Page<BlogCategory> findAllActive(Pageable pageable);
}