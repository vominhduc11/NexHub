package com.devwonder.product_service.repository;

import com.devwonder.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    Page<Product> findAllActive(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.availabilityStatus = 'AVAILABLE' ORDER BY p.popularity DESC, p.createdAt DESC")
    Page<Product> findAvailableProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.availabilityStatus = 'AVAILABLE' AND p.category.id = :categoryId ORDER BY p.popularity DESC")
    Page<Product> findAvailableProductsByCategory(Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.availabilityStatus = 'AVAILABLE' AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.popularity DESC")
    Page<Product> findAvailableProductsByKeyword(String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.sku = :sku")
    Optional<Product> findActiveBySku(String sku);
    
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.deletedAt IS NULL AND p.sku = :sku")
    boolean existsActiveBySku(String sku);
    
    boolean existsBySku(String sku);
}