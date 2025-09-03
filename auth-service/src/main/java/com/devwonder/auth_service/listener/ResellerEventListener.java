package com.devwonder.auth_service.listener;

import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.event.ResellerDeletedEvent;
import com.devwonder.auth_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResellerEventListener {

    private final AccountRepository accountRepository;

    @KafkaListener(topics = "reseller-deleted", groupId = "auth-service-group")
    @Transactional
    public void handleResellerDeleted(ResellerDeletedEvent event) {
        log.info("Received reseller-deleted event for accountId: {}, reseller: {}", 
                event.getAccountId(), event.getResellerName());

        try {
            Optional<Account> accountOpt = accountRepository.findById(event.getAccountId());
            
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                
                // Hybrid approach: Just audit/log, don't disable account
                // This allows for potential restoration if needed
                log.info("Reseller '{}' (accountId: {}) was deleted at {} for reason: {}", 
                        event.getResellerName(), 
                        event.getAccountId(), 
                        event.getDeletedAt(), 
                        event.getReason());

                // Optional: Could add a soft delete here if needed
                // account.setDeletedAt(LocalDateTime.now());
                // accountRepository.save(account);
                
                log.info("Successfully processed reseller deletion event for accountId: {}", event.getAccountId());
                
            } else {
                log.warn("Account with ID {} not found for reseller deletion event", event.getAccountId());
            }
            
        } catch (Exception e) {
            log.error("Error processing reseller-deleted event for accountId: {}, error: {}", 
                    event.getAccountId(), e.getMessage(), e);
            // Don't rethrow - let Kafka handle retry logic
        }
    }
}