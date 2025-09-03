package com.devwonder.auth_service.service;

import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.event.ResellerDeletedEvent;
import com.devwonder.auth_service.event.ResellerRestoredEvent;
import com.devwonder.auth_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for handling reseller-related events from other services
 * Provides business logic for reseller lifecycle management in auth service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ResellerEventService {

    private final AccountRepository accountRepository;

    /**
     * Processes reseller deletion event from user-service
     * Handles account soft deletion and audit trail
     * 
     * @param event The reseller deletion event
     * @throws IllegalArgumentException if accountId is null
     */
    /**
     * Validates the incoming event
     */
    private void validateEvent(ResellerDeletedEvent event) {
        if (event == null || event.getAccountId() == null) {
            throw new IllegalArgumentException("Invalid reseller deletion event: accountId cannot be null");
        }
    }

    @Transactional
    public void processResellerDeletion(ResellerDeletedEvent event) {
        validateEvent(event);
        
        log.info("Processing reseller deletion for accountId: {}, reseller: {}", 
                event.getAccountId(), event.getResellerName());

        Optional<Account> accountOpt = accountRepository.findById(event.getAccountId());
        
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            
            // Check if account is already soft deleted
            if (account.getDeletedAt() != null) {
                log.warn("Account {} is already soft deleted, skipping deletion", event.getAccountId());
                return;
            }
            
            // Perform soft delete
            softDeleteAccount(account);
            
            log.info("Successfully processed reseller deletion for accountId: {}", event.getAccountId());
            
        } else {
            log.warn("Account with ID {} not found for reseller deletion event - may have been hard deleted", 
                    event.getAccountId());
        }
    }

    /**
     * Soft deletes an account and saves it
     */
    private void softDeleteAccount(Account account) {
        account.setDeletedAt(LocalDateTime.now());
        accountRepository.save(account);
        
        log.info("Account {} soft deleted due to reseller deletion", account.getId());
    }

    /**
     * Processes reseller restoration event from user-service
     * Handles account restoration and audit trail
     * 
     * @param event The reseller restoration event
     * @throws IllegalArgumentException if accountId is null
     */
    @Transactional
    public void processResellerRestoration(ResellerRestoredEvent event) {
        if (event == null || event.getAccountId() == null) {
            throw new IllegalArgumentException("Invalid reseller restoration event: accountId cannot be null");
        }
        
        log.info("Processing reseller restoration for accountId: {}, reseller: {}", 
                event.getAccountId(), event.getResellerName());

        Optional<Account> accountOpt = accountRepository.findById(event.getAccountId());
        
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            
            // Check if account is already active
            if (account.getDeletedAt() == null) {
                log.warn("Account {} is already active, skipping restoration", event.getAccountId());
                return;
            }
            
            // Restore account
            account.setDeletedAt(null);
            accountRepository.save(account);
            
            log.info("Successfully processed reseller restoration for accountId: {}", event.getAccountId());
            
        } else {
            log.warn("Account with ID {} not found for reseller restoration event", 
                    event.getAccountId());
        }
    }
}