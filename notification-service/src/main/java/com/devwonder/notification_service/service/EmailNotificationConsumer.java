package com.devwonder.notification_service.service;

import com.devwonder.notification_service.controller.NotificationWebSocketController;
import com.devwonder.notification_service.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationConsumer {
    
    private final EmailService emailService;
    private final NotificationWebSocketController webSocketController;
    
    @KafkaListener(
        topics = "${kafka.topic.email:email-notifications}",
        containerFactory = "notificationEventKafkaListenerContainerFactory"
    )
    public void consumeEmailNotification(NotificationEvent event) {
        try {
            log.info("Received email notification event: {} for account ID: {}", 
                    event.getEventType(), event.getAccountId());
            
            if ("SEND_EMAIL".equals(event.getEventType())) {
                emailService.sendEmail(
                    event.getEmail(),
                    event.getSubject(),
                    event.getMessage()
                );
                
                // Send WebSocket notification for dealer registration
                if (event.getSubject() != null && event.getSubject().contains("Reseller Account Created")) {
                    webSocketController.broadcastDealerRegistration(
                        event.getUsername(),
                        event.getName(),
                        event.getEmail()
                    );
                }
                
                log.info("Email notification processed for account ID: {}", event.getAccountId());
            } else {
                log.warn("Unknown event type: {}, skipping", event.getEventType());
            }
            
        } catch (Exception e) {
            log.error("Failed to process email notification for account ID: {}", 
                     event.getAccountId(), e);
        }
    }
}