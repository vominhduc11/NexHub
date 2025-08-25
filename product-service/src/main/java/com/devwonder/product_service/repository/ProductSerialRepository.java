package com.devwonder.product_service.repository;

import com.devwonder.product_service.entity.ProductSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSerialRepository extends JpaRepository<ProductSerial, Long> {
    boolean existsById(Long id);
}