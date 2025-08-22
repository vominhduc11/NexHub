package com.devwonder.auth_service.component;

import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.entity.Role;
import com.devwonder.auth_service.repository.AccountRepository;
import com.devwonder.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing auth-service data...");
        
        try {
            // Create roles if not exist
            createRoleIfNotExists(1L, "CUSTOMER");
            createRoleIfNotExists(2L, "RESELLER"); 
            createRoleIfNotExists(3L, "ADMIN");
            
            // Create test accounts if not exist
            createTestAccountIfNotExists("admin", "admin123", "ADMIN");
            createTestAccountIfNotExists("customer", "customer123", "CUSTOMER");
            createTestAccountIfNotExists("reseller", "reseller123", "RESELLER");
            
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
    
    private void createTestAccountIfNotExists(String username, String password, String roleName) {
        if (accountRepository.findByUsername(username).isEmpty()) {
            // Create account first without role
            Account account = new Account();
            account.setUsername(username);
            account.setPassword(passwordEncoder.encode(password));
            
            // Save account first
            Account savedAccount = accountRepository.save(account);
            log.info("Created {} account", username);
            
            // Then find and assign role
            Role role = roleRepository.findByName(roleName);
            if (role != null) {
                savedAccount.getRoles().add(role);
                accountRepository.save(savedAccount);
                log.info("Assigned {} role to {} account", roleName, username);
            }
        }
    }
}