package com.devwonder.user_service.service;

import com.devwonder.user_service.dto.CreateResellerRequest;
import com.devwonder.user_service.dto.ResellerResponse;
import com.devwonder.user_service.entity.Reseller;
import com.devwonder.user_service.enums.ApprovalStatus;
import com.devwonder.user_service.event.ResellerApprovedEvent;
import com.devwonder.user_service.event.ResellerDeletedEvent;
import com.devwonder.user_service.event.ResellerRejectedEvent;
import com.devwonder.user_service.event.ResellerRestoredEvent;
import com.devwonder.user_service.exception.EmailAlreadyExistsException;
import com.devwonder.user_service.exception.PhoneAlreadyExistsException;
import com.devwonder.user_service.mapper.ResellerMapper;
import com.devwonder.user_service.repository.ResellerRepository;
import com.devwonder.user_service.exception.ResellerNotFoundException;
import com.devwonder.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResellerService {
    
    private final ResellerRepository resellerRepository;
    private final ResellerMapper resellerMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Transactional
    public ResellerResponse createReseller(CreateResellerRequest request) {
        log.info("Creating reseller for account ID: {}", request.getAccountId());
        
        // Check if reseller already exists
        if (resellerRepository.existsById(request.getAccountId())) {
            throw new ValidationException("Reseller with account ID " + request.getAccountId() + " already exists");
        }
        
        // Check if phone already exists
        if (resellerRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new PhoneAlreadyExistsException("Phone number '" + request.getPhone() + "' already exists");
        }
        
        // Check if email already exists
        if (resellerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email '" + request.getEmail() + "' already exists");
        }
        
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