package com.devwonder.auth_service.component;

import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.entity.Permission;
import com.devwonder.auth_service.entity.Role;
import com.devwonder.auth_service.repository.AccountRepository;
import com.devwonder.auth_service.repository.PermissionRepository;
import com.devwonder.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Main initialization method that runs on application startup.
     * Initializes permissions, roles, and test accounts.
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Initializing auth-service data...");
        
        try {
            // Initialize permissions first
            initializePermissions();
            
            // Then initialize roles with permissions
            initializeRoles();
            
            // Create test accounts if not exist
            createTestAccountIfNotExists("admin", "admin123", "ADMIN");
            createTestAccountIfNotExists("dealer", "dealer123", "DEALER");
            createTestAccountIfNotExists("customer", "customer123", "CUSTOMER");
            
            log.info("Auth-service data initialization completed successfully");
        } catch (Exception e) {
            log.error("Failed to initialize auth-service data: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Initialize all system permissions for different modules.
     * Creates permissions for User, Product, Blog, Warranty, and Notification modules.
     */
    private void initializePermissions() {
        // User permissions
        createPermissionIfNotExists("USER_CREATE");
        createPermissionIfNotExists("USER_READ");
        createPermissionIfNotExists("USER_UPDATE");
        createPermissionIfNotExists("USER_DELETE");
        
        // Product permissions
        createPermissionIfNotExists("PRODUCT_CREATE");
        createPermissionIfNotExists("PRODUCT_UPDATE");
        createPermissionIfNotExists("PRODUCT_DELETE");
        createPermissionIfNotExists("PRODUCT_READ");
        
        // Blog permissions
        createPermissionIfNotExists("BLOG_CREATE");
        createPermissionIfNotExists("BLOG_UPDATE");
        createPermissionIfNotExists("BLOG_DELETE");
        createPermissionIfNotExists("BLOG_READ");
        
        // Warranty permissions
        createPermissionIfNotExists("WARRANTY_CREATE");
        createPermissionIfNotExists("WARRANTY_READ");
        createPermissionIfNotExists("WARRANTY_UPDATE");
        createPermissionIfNotExists("WARRANTY_DELETE");
        
        // Notification permissions
        createPermissionIfNotExists("NOTIFICATION_CREATE");
        createPermissionIfNotExists("NOTIFICATION_READ");
        createPermissionIfNotExists("NOTIFICATION_UPDATE");
        createPermissionIfNotExists("NOTIFICATION_DELETE");

        // Reseller permissions
        createPermissionIfNotExists("RESELLER_CREATE");
        createPermissionIfNotExists("RESELLER_READ");
        createPermissionIfNotExists("RESELLER_UPDATE");
        createPermissionIfNotExists("RESELLER_DELETE");
        createPermissionIfNotExists("RESELLER_RESTORE");
        createPermissionIfNotExists("RESELLER_APPROVE");
        createPermissionIfNotExists("RESELLER_REJECT");

    }
    
    /**
     * Initialize system roles and assign permissions.
     * Creates ADMIN, DEALER, and CUSTOMER roles with their respective permissions.
     */
    private void initializeRoles() {
        createRoleWithPermissions("ADMIN", Set.of("NOTIFICATION_READ", "NOTIFICATION_UPDATE", "RESELLER_READ", "RESELLER_DELETE", "RESELLER_APPROVE", "RESELLER_REJECT", "RESELLER_RESTORE"));
        
        createRoleWithPermissions("DEALER", Set.of());
        
        createRoleWithPermissions("CUSTOMER", Set.of());
    }
    
    /**
     * Create a permission if it doesn't already exist.
     * @param name The permission name to create
     */
    private void createPermissionIfNotExists(String name) {
        if (!permissionRepository.existsByName(name)) {
            Permission permission = new Permission();
            permission.setName(name);
            permissionRepository.save(permission);
            log.info("Created permission: {}", name);
        }
    }
    
    /**
     * Create a role with specified permissions or update existing role.
     * @param roleName The name of the role to create
     * @param permissionNames Set of permission names to assign to the role
     */
    private void createRoleWithPermissions(String roleName, Set<String> permissionNames) {
        Role existingRole = roleRepository.findByName(roleName);
        if (existingRole == null) {
            Role role = new Role();
            role.setName(roleName);
            
            Set<Permission> permissions = new HashSet<>();
            for (String permissionName : permissionNames) {
                Permission permission = permissionRepository.findByName(permissionName)
                    .orElse(null);
                if (permission != null) {
                    permissions.add(permission);
                    log.debug("Added permission {} to role {}", permissionName, roleName);
                } else {
                    log.warn("Permission {} not found for role {}", permissionName, roleName);
                }
            }
            role.setPermissions(permissions);
            
            Role savedRole = roleRepository.save(role);
            log.info("Created role: {} with {} permissions", roleName, permissions.size());
            
            // Verify permissions were saved
            Role verifyRole = roleRepository.findByName(roleName);
            if (verifyRole != null) {
                log.info("Verified role {} has {} permissions in database",
                    roleName, verifyRole.getPermissions().size());
            }
        } else {
            // Update existing role with permissions if it has no permissions
            if (existingRole.getPermissions().isEmpty()) {
                Set<Permission> permissions = new HashSet<>();
                for (String permissionName : permissionNames) {
                    Permission permission = permissionRepository.findByName(permissionName)
                        .orElse(null);
                    if (permission != null) {
                        permissions.add(permission);
                    }
                }
                existingRole.setPermissions(permissions);
                roleRepository.save(existingRole);
                log.info("Updated existing role: {} with {} permissions", roleName, permissions.size());
            }
        }
    }
    
    /**
     * Create test account if it doesn't exist and assign specified role.
     * @param username The username for the account
     * @param password The password for the account
     * @param roleName The role name to assign to the account
     */
    private void createTestAccountIfNotExists(String username, String password, String roleName) {
        try {
            if (accountRepository.findByUsername(username).isEmpty()) {
                // Create account first without role
                Account account = new Account();
                account.setUsername(username);
                account.setPassword(passwordEncoder.encode(password));
                
                // Set account type based on role name
                switch (roleName.toUpperCase()) {
                    case "ADMIN":
                        account.setType(com.devwonder.auth_service.enums.AccountType.ADMIN);
                        break;
                    case "DEALER":
                        account.setType(com.devwonder.auth_service.enums.AccountType.DEALER);
                        break;
                    case "CUSTOMER":
                        account.setType(com.devwonder.auth_service.enums.AccountType.CUSTOMER);
                        break;
                    default:
                        account.setType(com.devwonder.auth_service.enums.AccountType.CUSTOMER);
                }
                
                // Set initial status based on account type
                account.setInitialStatus();
                
                // Save account first (let database generate ID)
                Account savedAccount = accountRepository.save(account);
                log.info("Created {} account with ID: {}", username, savedAccount.getId());
                
                // Then find and assign role
                Role role = roleRepository.findByName(roleName);
                if (role != null) {
                    // Reload the account to ensure it's properly managed
                    Account managedAccount = accountRepository.findById(savedAccount.getId()).orElse(null);
                    if (managedAccount != null) {
                        managedAccount.getRoles().add(role);
                        accountRepository.save(managedAccount);
                        log.info("Assigned {} role to {} account", roleName, username);
                    }
                }
            } else {
                log.info("Account {} already exists, skipping creation", username);
            }
        } catch (Exception e) {
            log.warn("Failed to create account {}: {} - This is likely not critical", username, e.getMessage());
        }
    }
}