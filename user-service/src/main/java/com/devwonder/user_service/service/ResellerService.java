package com.devwonder.user_service.service;

import com.devwonder.user_service.dto.CreateResellerRequest;
import com.devwonder.user_service.dto.ResellerResponse;
import com.devwonder.user_service.entity.Reseller;
import com.devwonder.user_service.repository.ResellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResellerService {
    
    private final ResellerRepository resellerRepository;
    
    @Transactional
    public ResellerResponse createReseller(CreateResellerRequest request) {
        log.info("Creating reseller for account ID: {}", request.getAccountId());
        
        // Check if reseller already exists
        if (resellerRepository.existsById(request.getAccountId())) {
            throw new RuntimeException("Reseller with account ID " + request.getAccountId() + " already exists");
        }
        
        // Create new reseller
        Reseller reseller = new Reseller();
        reseller.setAccountId(request.getAccountId());
        reseller.setName(request.getName());
        reseller.setAddress(request.getAddress());
        reseller.setPhone(request.getPhone());
        reseller.setEmail(request.getEmail());
        reseller.setDistrict(request.getDistrict());
        reseller.setCity(request.getCity());
        
        Reseller savedReseller = resellerRepository.save(reseller);
        
        log.info("Successfully created reseller for account ID: {}", savedReseller.getAccountId());
        
        return new ResellerResponse(
            savedReseller.getAccountId(),
            savedReseller.getName(),
            savedReseller.getAddress(),
            savedReseller.getPhone(),
            savedReseller.getEmail(),
            savedReseller.getDistrict(),
            savedReseller.getCity(),
            savedReseller.getCreatedAt(),
            savedReseller.getUpdatedAt()
        );
    }
}