package com.devwonder.blog_service.repository;

import com.devwonder.blog_service.entity.BlogAuthor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogAuthorRepository extends JpaRepository<BlogAuthor, Long> {
    
    // Find authors with posts
    @Query("SELECT a FROM BlogAuthor a WHERE a.articlesCount > 0 ORDER BY a.articlesCount DESC, a.name ASC")
    Page<BlogAuthor> findAuthorsWithPosts(Pageable pageable);
    
    @Query("SELECT a FROM BlogAuthor a WHERE a.articlesCount > 0 ORDER BY a.articlesCount DESC, a.name ASC")
    List<BlogAuthor> findAuthorsWithPosts();
    
    // Update articles count
    @Modifying
    @Query("UPDATE BlogAuthor a SET a.articlesCount = (SELECT COUNT(p) FROM BlogPost p WHERE p.author.id = a.id AND p.status = 'PUBLISHED') WHERE a.id = :authorId")
    void updateArticlesCount(@Param("authorId") Long authorId);
    
    // Update articles count for all authors
    @Modifying
    @Query("UPDATE BlogAuthor a SET a.articlesCount = (SELECT COUNT(p) FROM BlogPost p WHERE p.author.id = a.id AND p.status = 'PUBLISHED')")
    void updateAllArticlesCounts();
}