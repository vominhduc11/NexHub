package com.devwonder.user_service.component;

import com.devwonder.user_service.entity.Admin;
import com.devwonder.user_service.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing user-service data...");
        
        try {
            // Create admin record if not exist
            createAdminIfNotExists();
            
            log.info("User-service data initialization completed successfully");
        } catch (Exception e) {
            log.error("Failed to initialize user-service data: {}", e.getMessage(), e);
        }
    }
    
    private void createAdminIfNotExists() {
        // Check if admin record exists (account_id = 1)
        Admin existingAdmin = adminRepository.findByAccountId(1L);
        
        if (existingAdmin == null) {
            Admin admin = new Admin();
            admin.setAccountId(1L);  // corresponds to admin account in auth-service
            admin.setUsername("admin");
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            
            adminRepository.save(admin);
            log.info("Created admin record with account_id=1");
        }
    }
}