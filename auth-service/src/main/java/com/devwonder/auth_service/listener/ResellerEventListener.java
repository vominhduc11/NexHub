package com.devwonder.auth_service.listener;

import com.devwonder.auth_service.event.ResellerDeletedEvent;
import com.devwonder.auth_service.event.ResellerRestoredEvent;
import com.devwonder.auth_service.service.ResellerEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka event listener for reseller-related events
 * Delegates business logic processing to ResellerEventService
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ResellerEventListener {

    private final ResellerEventService resellerEventService;

    /**
     * Handles reseller deletion events from user-service
     * Delegates to service layer for business logic processing
     */
    @KafkaListener(
        topics = "${kafka.topic.reseller-deleted:reseller-deleted}", 
        groupId = "${kafka.consumer.group-id:auth-service-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleResellerDeleted(ResellerDeletedEvent event) {
        if (event == null || event.getAccountId() == null) {
            log.warn("Received invalid reseller-deleted event: {}", event);
            return;
        }

        log.info("Received reseller-deleted event for accountId: {}, reseller: {}", 
                event.getAccountId(), event.getResellerName());

        try {
            resellerEventService.processResellerDeletion(event);
            log.info("Successfully processed reseller deletion for accountId: {}", 
                    event.getAccountId());
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid event data for accountId: {}, error: {}", 
                    event.getAccountId(), e.getMessage());
            // Don't retry for validation errors
            
        } catch (Exception e) {
            log.error("Error processing reseller-deleted event for accountId: {}, error: {}", 
                    event.getAccountId(), e.getMessage(), e);
            // Let Kafka handle retry logic based on configuration
            throw e; // Rethrow for retry mechanism
        }
    }

    /**
     * Handles reseller restoration events from user-service
     * Delegates to service layer for business logic processing
     */
    @KafkaListener(
        topics = "${kafka.topic.reseller-restored:reseller-restored}", 
        groupId = "${kafka.consumer.group-id:auth-service-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleResellerRestored(ResellerRestoredEvent event) {
        if (event == null || event.getAccountId() == null) {
            log.warn("Received invalid reseller-restored event: {}", event);
            return;
        }

        log.info("Received reseller-restored event for accountId: {}, reseller: {}", 
                event.getAccountId(), event.getResellerName());

        try {
            resellerEventService.processResellerRestoration(event);
            log.info("Successfully processed reseller restoration for accountId: {}", 
                    event.getAccountId());
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid event data for accountId: {}, error: {}", 
                    event.getAccountId(), e.getMessage());
            // Don't retry for validation errors
            
        } catch (Exception e) {
            log.error("Error processing reseller-restored event for accountId: {}, error: {}", 
                    event.getAccountId(), e.getMessage(), e);
            // Let Kafka handle retry logic based on configuration
            throw e; // Rethrow for retry mechanism
        }
    }
}