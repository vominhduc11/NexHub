package com.devwonder.auth_service.service;

import com.devwonder.auth_service.client.UserServiceClient;
import com.devwonder.auth_service.dto.CreateResellerRequest;
import com.devwonder.auth_service.dto.NotificationEvent;
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

import java.time.LocalDateTime;

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
    private final NotificationService notificationService;
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
            throw new RuntimeException("Failed to create reseller profile: " + e.getMessage());
        }
        
        log.info("Successfully registered reseller with ID: {}", savedAccount.getId());
        
        // Send async notification event
        NotificationEvent event = new NotificationEvent(
            "SEND_EMAIL",
            savedAccount.getId(),
            savedAccount.getUsername(),
            request.getEmail(),
            request.getName(),
            "Welcome to NexHub - Reseller Account Created",
            "Dear " + request.getName() + ",\n\nWelcome to NexHub! Your reseller account has been successfully created.\n\nUsername: " + savedAccount.getUsername() + "\n\nYou can now start using our platform to manage your business.\n\nBest regards,\nNexHub Team",
            LocalDateTime.now()
        );
        notificationService.sendNotificationEvent(event);
        
        return new ResellerRegistrationResponse(
            savedAccount.getId(), 
            savedAccount.getUsername(), 
            "Reseller account created successfully"
        );
    }
}