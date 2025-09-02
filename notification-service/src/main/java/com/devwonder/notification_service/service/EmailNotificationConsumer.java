package com.devwonder.notification_service.service;

import com.devwonder.notification_service.controller.NotificationWebSocketController;
import com.devwonder.notification_service.dto.NotificationEvent;
import com.devwonder.notification_service.entity.Notification;
import com.devwonder.notification_service.service.NotificationService;
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
    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.email:email-notifications}", containerFactory = "notificationEventKafkaListenerContainerFactory")
    public void consumeEmailNotification(NotificationEvent event) {
        try {
            log.info("Received email notification event: {} for account ID: {}", event.getEventType(),
                    event.getAccountId());

            if ("SEND_EMAIL".equals(event.getEventType())) {
                emailService.sendEmail(
                        event.getEmail(),
                        event.getSubject(),
                        event.getMessage());

                log.info("Email notification processed for account ID: {}", event.getAccountId());
            } else {
                log.warn("Unknown event type: {}, skipping", event.getEventType());
            }

        } catch (Exception e) {
            log.error("Failed to process email notification for account ID: {}", event.getAccountId(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topic.websocket:websocket-notifications}", containerFactory = "websocketNotificationKafkaListenerContainerFactory")
    public void consumeWebSocketNotification(NotificationEvent event) {
        try {
            log.info("Received WebSocket notification event: {} for account ID: {}", event.getEventType(),
                    event.getAccountId());

            if ("WEBSOCKET_DEALER_REGISTRATION".equals(event.getEventType())) {
                
                // 1. Save notification to database
                String dealerName = event.getName() != null ? event.getName() : event.getUsername();
                
                Notification savedNotification = notificationService.createDealerRegistrationNotification(dealerName, event.getUsername());
                log.info("✅ Saved dealer registration notification to database for: {} with ID: {}", event.getUsername(), savedNotification.getId());
                
                // 2. Send the saved notification record to WebSocket (ADMIN users only)
                webSocketController.broadcastDealerRegistration(savedNotification);
                log.info("✅ Dealer registration notification sent to ADMIN users for: {}", dealerName);
            } else {
                log.warn("Unknown WebSocket event type: {}, skipping", event.getEventType());
            }

        } catch (Exception e) {
            log.error("Failed to process WebSocket notification for account ID: {}", event.getAccountId(), e);
        }
    }
}