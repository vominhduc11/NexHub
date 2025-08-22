package com.devwonder.user_service.repository;

import com.devwonder.user_service.entity.Reseller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResellerRepository extends JpaRepository<Reseller, Long> {
}