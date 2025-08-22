package com.devwonder.user_service.repository;

import com.devwonder.user_service.entity.Reseller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResellerRepository extends JpaRepository<Reseller, Long> {
    Optional<Reseller> findByPhone(String phone);
    Optional<Reseller> findByEmail(String email);
}