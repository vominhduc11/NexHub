package com.devwonder.auth_service.component;

import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.entity.Role;
import com.devwonder.auth_service.repository.AccountRepository;
import com.devwonder.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing auth-service data...");
        
        try {
            // Create roles if not exist
            createRoleIfNotExists(1L, "CUSTOMER");
            createRoleIfNotExists(2L, "DEALER"); 
            createRoleIfNotExists(3L, "ADMIN");
            
            // Create admin account if not exist
            createAdminAccountIfNotExists();
            
            log.info("Auth-service data initialization completed successfully");
        } catch (Exception e) {
            log.error("Failed to initialize auth-service data: {}", e.getMessage(), e);
        }
    }
    
    private void createRoleIfNotExists(Long id, String name) {
        Role existingRole = roleRepository.findByName(name);
        if (existingRole == null) {
            Role role = new Role();
            role.setName(name);
            roleRepository.save(role);
            log.info("Created role: {}", name);
        }
    }
    
    private void createAdminAccountIfNotExists() {
        Account existingAdmin = accountRepository.findByUsername("admin");
        if (existingAdmin == null) {
            // Create admin account first without role
            Account admin = new Account();
            admin.setUsername("admin");
            admin.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMye2PAdPRmZUNNgR3z5V0zT7Xwx8VzU9Hm"); // admin123
            
            // Save account first
            Account savedAdmin = accountRepository.save(admin);
            log.info("Created admin account");
            
            // Then find and assign role
            Role adminRole = roleRepository.findByName("ADMIN");
            if (adminRole != null) {
                savedAdmin.getRoles().add(adminRole);
                accountRepository.save(savedAdmin);
                log.info("Assigned ADMIN role to admin account");
            }
        }
    }
}