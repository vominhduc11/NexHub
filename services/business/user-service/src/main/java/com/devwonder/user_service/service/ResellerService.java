package com.devwonder.user_service.service;

import com.devwonder.user_service.client.AuthServiceClient;
import com.devwonder.user_service.dto.CreateAccountRequest;
import com.devwonder.user_service.dto.CreateAccountResponse;
import com.devwonder.user_service.dto.CreateResellerRequest;
import com.devwonder.user_service.dto.ResellerRegistrationRequest;
import com.devwonder.user_service.dto.ResellerResponse;
import com.devwonder.user_service.entity.Reseller;
import com.devwonder.user_service.enums.ApprovalStatus;
import com.devwonder.user_service.event.ResellerApprovedEvent;
import com.devwonder.user_service.event.ResellerDeletedEvent;
import com.devwonder.user_service.event.ResellerRejectedEvent;
import com.devwonder.user_service.event.ResellerRestoredEvent;
import com.devwonder.user_service.exception.AuthServiceIntegrationException;
import com.devwonder.user_service.exception.EmailAlreadyExistsException;
import com.devwonder.user_service.exception.PhoneAlreadyExistsException;
import com.devwonder.user_service.mapper.ResellerMapper;
import com.devwonder.user_service.repository.ResellerRepository;
import com.devwonder.user_service.exception.ResellerNotFoundException;
import com.devwonder.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResellerService {
    
    private final ResellerRepository resellerRepository;
    private final ResellerMapper resellerMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuthServiceClient authServiceClient;
    
    @Value("${auth.api.key}")
    private String apiKey;
    
    @Transactional
    public ResellerResponse registerReseller(ResellerRegistrationRequest request) {
        log.info("=== ENTERING registerReseller method for username: {} ===", request.getUsername());
        
        CreateAccountResponse accountResponse = null;
        boolean accountCreated = false;
        
        try {
            // Step 1: Validate phone and email uniqueness first (fail fast)
            log.info("Validating phone and email uniqueness for: {}", request.getUsername());
            if (resellerRepository.findByPhone(request.getPhone()).isPresent()) {
                throw new PhoneAlreadyExistsException("Phone number '" + request.getPhone() + "' already exists");
            }
            if (resellerRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new EmailAlreadyExistsException("Email '" + request.getEmail() + "' already exists");
            }
            
            // Step 2: Create account in auth-service
            CreateAccountRequest accountRequest = new CreateAccountRequest(
                request.getUsername(),
                request.getPassword(),
                "DEALER"
            );
            
            log.info("Calling auth-service to create account for username: {}", request.getUsername());
            try {
                accountResponse = authServiceClient.createAccount(accountRequest, apiKey).getBody();
                if (accountResponse == null || accountResponse.getAccountId() == null) {
                    throw new AuthServiceIntegrationException("Failed to create account in auth-service - null response");
                }
                accountCreated = true;
                log.info("Successfully created account with ID: {}", accountResponse.getAccountId());
            } catch (Exception e) {
                log.error("Failed to create account in auth-service for username: {}", request.getUsername(), e);
                throw new AuthServiceIntegrationException("Failed to create account: " + e.getMessage(), e);
            }
            
            // Step 3: Create reseller profile (with compensation on failure)
            CreateResellerRequest createResellerRequest = new CreateResellerRequest(
                accountResponse.getAccountId(),
                request.getName(),
                request.getAddress(),
                request.getPhone(),
                request.getEmail(),
                request.getDistrict(),
                request.getCity()
            );
            
            ResellerResponse resellerResponse;
            try {
                resellerResponse = createReseller(createResellerRequest);
                log.info("Successfully created reseller profile for account ID: {}", accountResponse.getAccountId());
            } catch (Exception e) {
                log.error("Failed to create reseller profile for account ID: {}, initiating compensation", accountResponse.getAccountId(), e);
                
                // Compensation: Delete the account that was created
                compensateAccountCreation(accountResponse.getAccountId());
                
                // Re-throw the original exception
                throw e;
            }
            
            // Step 4: Send notification events (non-blocking)
            sendNotificationEventsAsync(accountResponse, request);
            
            log.info("Successfully registered reseller with account ID: {}", accountResponse.getAccountId());
            return resellerResponse;
            
        } catch (Exception e) {
            log.error("=== EXCEPTION in registerReseller: {} ===", e.getMessage(), e);
            
            // Additional compensation if needed and account was created
            if (accountCreated && accountResponse != null && !(e instanceof AuthServiceIntegrationException)) {
                compensateAccountCreation(accountResponse.getAccountId());
            }
            
            throw e;
        }
    }
    
    private void compensateAccountCreation(Long accountId) {
        log.info("=== COMPENSATION: Attempting to delete account with ID: {} ===", accountId);
        try {
            authServiceClient.deleteAccount(accountId, apiKey);
            log.info("Successfully deleted account {} during compensation", accountId);
        } catch (Exception compensationError) {
            log.error("CRITICAL: Failed to delete account {} during compensation: {}. Manual cleanup required!", 
                accountId, compensationError.getMessage());
            
            // Send alert event for manual cleanup
            try {
                Map<String, Object> alertEvent = new HashMap<>();
                alertEvent.put("type", "MANUAL_CLEANUP_REQUIRED");
                alertEvent.put("accountId", accountId);
                alertEvent.put("reason", "Failed to delete account during compensation rollback");
                alertEvent.put("error", compensationError.getMessage());
                alertEvent.put("timestamp", LocalDateTime.now());
                
                kafkaTemplate.send("system-alerts", alertEvent);
            } catch (Exception alertError) {
                log.error("Failed to send manual cleanup alert: {}", alertError.getMessage());
            }
        }
    }
    
    private void sendNotificationEventsAsync(CreateAccountResponse accountResponse, ResellerRegistrationRequest request) {
        try {
            // Send welcome email event
            kafkaTemplate.send("notification-email", createWelcomeEmailEvent(
                accountResponse.getAccountId(),
                request.getUsername(), 
                request.getEmail(),
                request.getName()
            ));
            
            // Send WebSocket notification for dealer registration
            kafkaTemplate.send("notification-websocket", createDealerRegistrationEvent(
                accountResponse.getAccountId(),
                request.getUsername(),
                request.getName()
            ));
            
            log.info("Successfully sent notification events for reseller registration");
        } catch (Exception e) {
            log.error("Failed to send notification events for reseller registration: {}", e.getMessage());
            // Don't fail the registration if notification fails - it's non-critical
        }
    }
    
    private Map<String, Object> createWelcomeEmailEvent(Long accountId, String username, String email, String name) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "SEND_EMAIL");
        event.put("accountId", accountId);
        event.put("username", username);
        event.put("email", email);
        event.put("name", name);
        event.put("subject", "Welcome to NexHub - Reseller Account Created");
        event.put("message", "Dear " + name + ",\n\nWelcome to NexHub! Your reseller account has been successfully created.\n\nUsername: " + username + "\n\nYou can now start using our platform to manage your business.\n\nBest regards,\nNexHub Team");
        event.put("timestamp", LocalDateTime.now());
        return event;
    }
    
    private Map<String, Object> createDealerRegistrationEvent(Long accountId, String username, String name) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "WEBSOCKET_DEALER_REGISTRATION");
        event.put("accountId", accountId);
        event.put("username", username);
        event.put("name", name);
        event.put("title", "New Dealer Registration");
        event.put("message", "A new dealer has been registered: " + name + " (" + username + ")");
        event.put("timestamp", LocalDateTime.now());
        return event;
    }
    
    @Transactional
    public ResellerResponse createReseller(CreateResellerRequest request) {
        log.info("Creating reseller for account ID: {}", request.getAccountId());
        
        // Check if reseller already exists
        if (resellerRepository.existsById(request.getAccountId())) {
            throw new ValidationException("Reseller with account ID " + request.getAccountId() + " already exists");
        }
        
        // Note: Phone and email validation is done in registerReseller method to fail fast
        // This method may be called independently for other use cases
        
        // Create new reseller using Builder pattern
        Reseller reseller = Reseller.builder()
                .accountId(request.getAccountId())
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .district(request.getDistrict())
                .city(request.getCity())
                .build();
        
        Reseller savedReseller = resellerRepository.save(reseller);
        
        log.info("Successfully created reseller for account ID: {}", savedReseller.getAccountId());
        
        return resellerMapper.toResponse(savedReseller);
    }

    @Transactional(readOnly = true)
    public Page<ResellerResponse> getAllActiveResellers(int page, int size) {
        log.info("Fetching all active resellers - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Reseller> resellers = resellerRepository.findAllActive(pageable);
        
        return resellers.map(resellerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ResellerResponse> getAllDeletedResellers(int page, int size) {
        log.info("Fetching all deleted resellers - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Reseller> deletedResellers = resellerRepository.findAllDeleted(pageable);
        
        return deletedResellers.map(resellerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Optional<ResellerResponse> findActiveById(Long accountId) {
        return resellerRepository.findActiveById(accountId)
            .map(resellerMapper::toResponse);
    }

    @Transactional
    public void softDeleteReseller(Long accountId) {
        log.info("Soft deleting reseller with account ID: {}", accountId);
        
        Reseller reseller = resellerRepository.findById(accountId)
            .orElseThrow(() -> new ResellerNotFoundException(accountId));

        if (reseller.getDeletedAt() != null) {
            throw new ValidationException("Reseller is already deleted");
        }

        // 1. Soft delete reseller
        reseller.setDeletedAt(LocalDateTime.now());
        Reseller deletedReseller = resellerRepository.save(reseller);
        log.info("Reseller soft deleted successfully: {}", deletedReseller.getName());
        
        // 2. Publish event to Kafka
        try {
            ResellerDeletedEvent event = ResellerDeletedEvent.of(
                accountId,
                deletedReseller.getName(),
                deletedReseller.getEmail(),
                "Admin deletion via API"
            );
            
            kafkaTemplate.send("reseller-deleted", event);
            log.info("Published reseller-deleted event for accountId: {}", accountId);
            
        } catch (Exception e) {
            log.error("Failed to publish reseller-deleted event for accountId: {}, error: {}", 
                accountId, e.getMessage());
            // Don't fail the deletion if event publishing fails
        }
    }

    @Transactional
    public void restoreReseller(Long accountId) {
        log.info("Restoring reseller with account ID: {}", accountId);
        
        Reseller reseller = resellerRepository.findById(accountId)
            .orElseThrow(() -> new ResellerNotFoundException(accountId));

        if (reseller.getDeletedAt() == null) {
            throw new ValidationException("Reseller is not deleted");
        }

        reseller.setDeletedAt(null);
        Reseller restoredReseller = resellerRepository.save(reseller);
        log.info("Reseller restored successfully: {}", restoredReseller.getName());
        
        // Publish event to Kafka
        try {
            ResellerRestoredEvent event = ResellerRestoredEvent.of(
                accountId,
                restoredReseller.getName(),
                restoredReseller.getEmail(),
                "Admin restoration via API"
            );
            
            kafkaTemplate.send("reseller-restored", event);
            log.info("Published reseller-restored event for accountId: {}", accountId);
            
        } catch (Exception e) {
            log.error("Failed to publish reseller-restored event for accountId: {}, error: {}", 
                accountId, e.getMessage());
            // Don't fail the restoration if event publishing fails
        }
    }

    @Transactional
    public void hardDeleteReseller(Long accountId) {
        log.info("Hard deleting reseller with account ID: {}", accountId);
        
        Reseller reseller = resellerRepository.findById(accountId)
            .orElseThrow(() -> new ResellerNotFoundException(accountId));

        resellerRepository.delete(reseller);
        log.info("Reseller hard deleted successfully: {}", reseller.getName());
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long accountId) {
        return resellerRepository.existsById(accountId);
    }

    @Transactional
    public void approveReseller(Long accountId, Long approvedBy) {
        log.info("Approving reseller for account ID: {} by user: {}", accountId, approvedBy);
        
        Reseller reseller = resellerRepository.findById(accountId)
            .orElseThrow(() -> new ResellerNotFoundException("Reseller with account ID " + accountId + " not found"));
        
        if (reseller.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new ValidationException("Reseller is not in pending status for approval");
        }
        
        reseller.setApprovalStatus(ApprovalStatus.APPROVED);
        reseller.setApprovedAt(LocalDateTime.now());
        reseller.setApprovedBy(approvedBy);
        
        Reseller approvedReseller = resellerRepository.save(reseller);
        
        log.info("Reseller approved successfully: {}", approvedReseller.getName());
        
        // Publish ResellerApprovedEvent for account activation and notifications
        try {
            ResellerApprovedEvent event = ResellerApprovedEvent.of(
                approvedReseller.getAccountId(),
                approvedReseller.getName(),
                approvedReseller.getEmail(),
                approvedBy,
                "Reseller registration approved"
            );
            kafkaTemplate.send("reseller-approved", event);
            log.info("Published reseller-approved event for accountId: {}", accountId);
        } catch (Exception e) {
            log.error("Failed to publish reseller-approved event for accountId: {}, error: {}", 
                accountId, e.getMessage());
        }
    }

    @Transactional
    public void rejectReseller(Long accountId, String reason, Long rejectedBy) {
        log.info("Rejecting reseller for account ID: {} with reason: {} by user: {}", accountId, reason, rejectedBy);
        
        Reseller reseller = resellerRepository.findById(accountId)
            .orElseThrow(() -> new ResellerNotFoundException("Reseller with account ID " + accountId + " not found"));
        
        if (reseller.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new ValidationException("Reseller is not in pending status for rejection");
        }
        
        reseller.setApprovalStatus(ApprovalStatus.REJECTED);
        reseller.setRejectionReason(reason);
        // Note: rejectedBy could be stored in a separate field if needed in future
        
        Reseller rejectedReseller = resellerRepository.save(reseller);
        
        log.info("Reseller rejected successfully: {}", rejectedReseller.getName());
        
        // Publish ResellerRejectedEvent for account deactivation and notifications
        try {
            ResellerRejectedEvent event = ResellerRejectedEvent.of(
                rejectedReseller.getAccountId(),
                rejectedReseller.getName(),
                rejectedReseller.getEmail(),
                rejectedBy,
                reason
            );
            kafkaTemplate.send("reseller-rejected", event);
            log.info("Published reseller-rejected event for accountId: {}", accountId);
        } catch (Exception e) {
            log.error("Failed to publish reseller-rejected event for accountId: {}, error: {}", 
                accountId, e.getMessage());
        }
    }
}