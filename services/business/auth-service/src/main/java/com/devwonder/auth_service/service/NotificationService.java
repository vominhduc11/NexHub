package com.devwonder.auth_service.service;

import com.devwonder.auth_service.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topic.email:email-notifications}")
    private String emailTopic;
    
    @Value("${kafka.topic.websocket:websocket-notifications}")
    private String websocketTopic;
    
    @Async
    public void sendNotificationEvent(NotificationEvent event) {
        try {
            String topic = determineTopicByEventType(event.getEventType());
            log.info("Sending {} notification to topic {}", event.getEventType(), topic);
            
            kafkaTemplate.send(topic, event.getAccountId().toString(), event);
            log.info("Successfully sent notification event for account ID: {}", event.getAccountId());
            
        } catch (Exception e) {
            log.error("Failed to send notification event for account ID: {}", event.getAccountId(), e);
        }
    }
    
    private String determineTopicByEventType(String eventType) {
        return switch (eventType) {
            case "SEND_EMAIL" -> emailTopic;
            case "WEBSOCKET_DEALER_REGISTRATION" -> websocketTopic;
            default -> emailTopic; // Default for backward compatibility
        };
    }
}