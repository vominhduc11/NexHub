package com.devwonder.auth_service.listener;

import com.devwonder.auth_service.event.ResellerApprovedEvent;
import com.devwonder.auth_service.event.ResellerDeletedEvent;
import com.devwonder.auth_service.event.ResellerRejectedEvent;
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
        containerFactory = "resellerDeletedKafkaListenerContainerFactory"
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
        containerFactory = "resellerRestoredKafkaListenerContainerFactory"
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

    /**
     * Handles reseller approval events from user-service
     * Activates account when reseller is approved
     */
    @KafkaListener(
        topics = "${kafka.topic.reseller-approved:reseller-approved}", 
        groupId = "${kafka.consumer.group-id:auth-service-group}",
        containerFactory = "resellerApprovedKafkaListenerContainerFactory"
    )
    public void handleResellerApproved(ResellerApprovedEvent event) {
        if (event == null || event.getAccountId() == null) {
            log.warn("Received invalid reseller-approved event: {}", event);
            return;
        }

        log.info("Received reseller-approved event for accountId: {}, reseller: {}", 
                event.getAccountId(), event.getResellerName());

        try {
            resellerEventService.processResellerApproval(event);
            log.info("Successfully processed reseller approval for accountId: {}", 
                    event.getAccountId());
            
        } catch (Exception e) {
            log.error("Error processing reseller-approved event for accountId: {}, error: {}", 
                    event.getAccountId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Handles reseller rejection events from user-service
     * Deactivates account when reseller is rejected
     */
    @KafkaListener(
        topics = "${kafka.topic.reseller-rejected:reseller-rejected}", 
        groupId = "${kafka.consumer.group-id:auth-service-group}",
        containerFactory = "resellerRejectedKafkaListenerContainerFactory"
    )
    public void handleResellerRejected(ResellerRejectedEvent event) {
        if (event == null || event.getAccountId() == null) {
            log.warn("Received invalid reseller-rejected event: {}", event);
            return;
        }

        log.info("Received reseller-rejected event for accountId: {}, reseller: {}, reason: {}", 
                event.getAccountId(), event.getResellerName(), event.getRejectionReason());

        try {
            resellerEventService.processResellerRejection(event);
            log.info("Successfully processed reseller rejection for accountId: {}", 
                    event.getAccountId());
            
        } catch (Exception e) {
            log.error("Error processing reseller-rejected event for accountId: {}, error: {}", 
                    event.getAccountId(), e.getMessage(), e);
            throw e;
        }
    }
}