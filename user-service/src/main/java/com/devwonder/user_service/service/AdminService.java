package com.devwonder.user_service.service;

import com.devwonder.common.exception.ValidationException;
import com.devwonder.user_service.dto.AdminResponse;
import com.devwonder.user_service.dto.CreateAdminRequest;
import com.devwonder.user_service.entity.Admin;
import com.devwonder.user_service.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    
    private final AdminRepository adminRepository;
    
    @Transactional
    public AdminResponse createAdmin(CreateAdminRequest request) {
        log.info("Creating admin for account ID: {}", request.getAccountId());
        
        // Check if admin already exists
        if (adminRepository.findByAccountId(request.getAccountId()) != null) {
            throw new ValidationException("Admin with account ID " + request.getAccountId() + " already exists");
        }
        
        // Create new admin
        Admin admin = new Admin();
        admin.setAccountId(request.getAccountId());
        admin.setUsername(request.getUsername());
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        
        Admin savedAdmin = adminRepository.save(admin);
        log.info("Successfully created admin for account ID: {}", savedAdmin.getAccountId());
        
        // Convert to response DTO
        AdminResponse response = new AdminResponse();
        response.setAccountId(savedAdmin.getAccountId());
        response.setUsername(savedAdmin.getUsername());
        response.setCreatedAt(savedAdmin.getCreatedAt());
        response.setUpdatedAt(savedAdmin.getUpdatedAt());
        
        return response;
    }
    
    @Transactional(readOnly = true)
    public boolean existsById(Long accountId) {
        return adminRepository.findByAccountId(accountId) != null;
    }
}