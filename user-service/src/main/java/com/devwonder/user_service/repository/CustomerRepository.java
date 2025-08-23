package com.devwonder.user_service.repository;

import com.devwonder.user_service.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL")
    Page<Customer> findAllActive(Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL AND c.accountId = :accountId")
    Optional<Customer> findActiveById(Long accountId);
    
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL AND c.name LIKE %:name%")
    Page<Customer> findActiveByNameContaining(String name, Pageable pageable);
}