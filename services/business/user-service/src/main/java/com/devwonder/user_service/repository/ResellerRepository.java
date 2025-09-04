package com.devwonder.user_service.repository;

import com.devwonder.user_service.entity.Reseller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResellerRepository extends JpaRepository<Reseller, Long> {
    
    @Query("SELECT r FROM Reseller r WHERE r.deletedAt IS NULL")
    Page<Reseller> findAllActive(Pageable pageable);
    
    @Query("SELECT r FROM Reseller r WHERE r.deletedAt IS NOT NULL")
    Page<Reseller> findAllDeleted(Pageable pageable);
    
    @Query("SELECT r FROM Reseller r WHERE r.deletedAt IS NULL AND r.accountId = :accountId")
    Optional<Reseller> findActiveById(Long accountId);
    
    @Query("SELECT r FROM Reseller r WHERE r.deletedAt IS NULL AND r.phone = :phone")
    Optional<Reseller> findActiveByPhone(String phone);
    
    @Query("SELECT r FROM Reseller r WHERE r.deletedAt IS NULL AND r.email = :email")
    Optional<Reseller> findActiveByEmail(String email);
    
    Optional<Reseller> findByPhone(String phone);
    Optional<Reseller> findByEmail(String email);
}