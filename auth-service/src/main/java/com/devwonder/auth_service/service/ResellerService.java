package com.devwonder.auth_service.service;

import com.devwonder.auth_service.client.UserServiceClient;
import com.devwonder.auth_service.dto.CreateResellerRequest;
import com.devwonder.auth_service.dto.ResellerRegistrationRequest;
import com.devwonder.auth_service.dto.ResellerRegistrationResponse;
import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.entity.Role;
import com.devwonder.auth_service.exception.RoleNotFoundException;
import com.devwonder.auth_service.exception.UsernameAlreadyExistsException;
import com.devwonder.auth_service.repository.AccountRepository;
import com.devwonder.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResellerService {
    
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final UserServiceClient userServiceClient;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Transactional
    public ResellerRegistrationResponse registerReseller(ResellerRegistrationRequest request) {
        log.info("Registering new reseller with username: {}", request.getUsername());
        
        // Check if username already exists
        if (accountRepository.findByUsername(request.getUsername()) != null) {
            throw new UsernameAlreadyExistsException("Username '" + request.getUsername() + "' already exists");
        }
        
        // Find DEALER role
        Role dealerRole = roleRepository.findByName("DEALER");
        if (dealerRole == null) {
            throw new RoleNotFoundException("DEALER role not found in database");
        }
        
        // Create new account
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Save account first
        Account savedAccount = accountRepository.save(account);
        
        // Assign DEALER role
        savedAccount.getRoles().add(dealerRole);
        accountRepository.save(savedAccount);
        
        try {
            // Create reseller profile in user-service
            CreateResellerRequest createResellerRequest = new CreateResellerRequest(
                savedAccount.getId(),
                request.getName(),
                request.getAddress(),
                request.getPhone(),
                request.getEmail(),
                request.getDistrict(),
                request.getCity()
            );
            
            log.info("Calling user-service to create reseller profile for account ID: {}", savedAccount.getId());
            userServiceClient.createReseller(createResellerRequest, "AUTH_SERVICE_SECRET_2024_NEXHUB");
            log.info("Successfully created reseller profile in user-service");
            
        } catch (Exception e) {
            log.error("Failed to create reseller profile in user-service for account ID: {}", savedAccount.getId(), e);
            // Rollback account creation
            accountRepository.delete(savedAccount);
            throw new RuntimeException("Failed to create reseller profile: " + e.getMessage());
        }
        
        log.info("Successfully registered reseller with ID: {}", savedAccount.getId());
        
        return new ResellerRegistrationResponse(
            savedAccount.getId(), 
            savedAccount.getUsername(), 
            "Reseller account created successfully"
        );
    }
}