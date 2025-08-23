package com.devwonder.product_service.repository;

import com.devwonder.product_service.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL")
    Page<Category> findAllActive(Pageable pageable);
    
    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL AND c.id = :id")
    Optional<Category> findActiveById(Long id);
    
    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL AND c.slug = :slug")
    Optional<Category> findActiveBySlug(String slug);
}