package com.devwonder.blog_service.repository;

import com.devwonder.blog_service.entity.BlogTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BlogTagRepository extends JpaRepository<BlogTag, Long> {
    
    // Find tags with posts
    @Query("SELECT t FROM BlogTag t WHERE t.postsCount > 0 ORDER BY t.postsCount DESC, t.name ASC")
    List<BlogTag> findTagsWithPosts();
    
    @Query("SELECT t FROM BlogTag t WHERE t.postsCount > 0 ORDER BY t.postsCount DESC, t.name ASC")
    Page<BlogTag> findTagsWithPosts(Pageable pageable);
    
    // Find popular tags
    @Query("SELECT t FROM BlogTag t WHERE t.postsCount > 0 ORDER BY t.postsCount DESC")
    List<BlogTag> findPopularTags(Pageable pageable);
    
    // Find by slug
    Optional<BlogTag> findBySlug(String slug);
    
    // Find by slugs
    Set<BlogTag> findBySlugIn(Set<String> slugs);
    
    // Find by IDs
    Set<BlogTag> findByIdIn(Set<Long> ids);
    
    // Check if slug exists
    boolean existsBySlug(String slug);
    
    // Update posts count
    @Modifying
    @Query("UPDATE BlogTag t SET t.postsCount = (SELECT COUNT(p) FROM BlogPost p JOIN p.tags pt WHERE pt.id = t.id AND p.status = 'PUBLISHED') WHERE t.id = :tagId")
    void updatePostsCount(@Param("tagId") Long tagId);
    
    // Update posts count for all tags
    @Modifying
    @Query("UPDATE BlogTag t SET t.postsCount = (SELECT COUNT(p) FROM BlogPost p JOIN p.tags pt WHERE pt.id = t.id AND p.status = 'PUBLISHED')")
    void updateAllPostsCounts();
}