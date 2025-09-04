package com.devwonder.warranty_service.repository;

import com.devwonder.warranty_service.entity.PurchasedProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchasedProductRepository extends JpaRepository<PurchasedProduct, Long> {
    
    // Override default methods to exclude soft deleted
    @Override
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.deletedAt IS NULL")
    @NonNull Page<PurchasedProduct> findAll(Pageable pageable);
    
    @Override
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.id = :id AND pp.deletedAt IS NULL")
    @NonNull Optional<PurchasedProduct> findById(@Param("id") Long id);
    
    // Find by customer ID
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.idCustomer = :customerId AND pp.deletedAt IS NULL ORDER BY pp.purchaseDate DESC")
    Page<PurchasedProduct> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
    
    // Find by reseller ID
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.idReseller = :resellerId AND pp.deletedAt IS NULL ORDER BY pp.purchaseDate DESC")
    Page<PurchasedProduct> findByResellerId(@Param("resellerId") Long resellerId, Pageable pageable);
    
    // Find by product serial ID
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.idProductSerial = :productSerialId AND pp.deletedAt IS NULL ORDER BY pp.purchaseDate DESC")
    Page<PurchasedProduct> findByProductSerialId(@Param("productSerialId") Long productSerialId, Pageable pageable);
    
    // Find active warranties (not expired)
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.expirationDate >= CURRENT_DATE AND pp.deletedAt IS NULL ORDER BY pp.expirationDate ASC")
    Page<PurchasedProduct> findActiveWarranties(Pageable pageable);
    
    // Find expired warranties
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.expirationDate < CURRENT_DATE AND pp.deletedAt IS NULL ORDER BY pp.expirationDate DESC")
    Page<PurchasedProduct> findExpiredWarranties(Pageable pageable);
    
    // Find warranties expiring soon (within days)
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.expirationDate BETWEEN CURRENT_DATE AND :targetDate AND pp.deletedAt IS NULL ORDER BY pp.expirationDate ASC")
    Page<PurchasedProduct> findWarrantiesExpiringSoon(@Param("targetDate") LocalDate targetDate, Pageable pageable);
    
    // Find warranties expiring within specific days for customer
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.idCustomer = :customerId " +
           "AND pp.expirationDate BETWEEN CURRENT_DATE AND :targetDate AND pp.deletedAt IS NULL ORDER BY pp.expirationDate ASC")
    List<PurchasedProduct> findCustomerWarrantiesExpiringSoon(@Param("customerId") Long customerId, @Param("targetDate") LocalDate targetDate);
    
    // Find by product serial and customer
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.idProductSerial = :productSerialId AND pp.idCustomer = :customerId AND pp.deletedAt IS NULL")
    Optional<PurchasedProduct> findByIdProductSerialAndIdCustomer(@Param("productSerialId") Long productSerialId, @Param("customerId") Long customerId);
    
    // Check if product serial is already registered
    @Query("SELECT COUNT(pp) > 0 FROM PurchasedProduct pp WHERE pp.idProductSerial = :productSerialId AND pp.deletedAt IS NULL")
    boolean existsByIdProductSerial(@Param("productSerialId") Long productSerialId);
    
    // Update warranty remaining days (PostgreSQL compatible)
    @Modifying
    @Query("UPDATE PurchasedProduct pp SET pp.warrantyRemainingDays = " +
           "CASE WHEN pp.expirationDate >= CURRENT_DATE " +
           "THEN CAST((pp.expirationDate - CURRENT_DATE) AS INTEGER) " +
           "ELSE 0 END " +
           "WHERE pp.id = :id AND pp.deletedAt IS NULL")
    void updateWarrantyRemainingDays(@Param("id") Long id);
    
    // Update all warranty remaining days (PostgreSQL compatible)
    @Modifying
    @Query("UPDATE PurchasedProduct pp SET pp.warrantyRemainingDays = " +
           "CASE WHEN pp.expirationDate >= CURRENT_DATE " +
           "THEN CAST((pp.expirationDate - CURRENT_DATE) AS INTEGER) " +
           "ELSE 0 END " +
           "WHERE pp.deletedAt IS NULL")
    void updateAllWarrantyRemainingDays();
    
    // Statistics queries
    @Query("SELECT COUNT(pp) FROM PurchasedProduct pp WHERE pp.expirationDate >= CURRENT_DATE AND pp.deletedAt IS NULL")
    Long countActiveWarranties();
    
    @Query("SELECT COUNT(pp) FROM PurchasedProduct pp WHERE pp.expirationDate < CURRENT_DATE AND pp.deletedAt IS NULL")
    Long countExpiredWarranties();
    
    @Query("SELECT COUNT(pp) FROM PurchasedProduct pp WHERE pp.expirationDate BETWEEN CURRENT_DATE AND :targetDate AND pp.deletedAt IS NULL")
    Long countWarrantiesExpiringSoon(@Param("targetDate") LocalDate targetDate);
    
    @Query("SELECT COUNT(pp) FROM PurchasedProduct pp WHERE pp.idCustomer = :customerId AND pp.deletedAt IS NULL")
    Long countByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT COUNT(pp) FROM PurchasedProduct pp WHERE pp.idReseller = :resellerId AND pp.deletedAt IS NULL")
    Long countByResellerId(@Param("resellerId") Long resellerId);
    
    // Date range queries
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.purchaseDate BETWEEN :startDate AND :endDate AND pp.deletedAt IS NULL ORDER BY pp.purchaseDate DESC")
    Page<PurchasedProduct> findByPurchaseDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
    
    // Soft delete methods
    @Modifying
    @Query("UPDATE PurchasedProduct pp SET pp.deletedAt = CURRENT_TIMESTAMP WHERE pp.id = :id")
    void softDelete(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE PurchasedProduct pp SET pp.deletedAt = NULL WHERE pp.id = :id")
    void restore(@Param("id") Long id);
    
    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.deletedAt IS NULL ORDER BY pp.createdAt DESC")
    Page<PurchasedProduct> findAllActive(Pageable pageable);
}