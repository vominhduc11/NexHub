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
    
    @Async
    public void sendNotificationEvent(NotificationEvent event) {
        try {
            log.info("Sending email notification to topic {}: {}", emailTopic, event.getEventType());
            kafkaTemplate.send(emailTopic, event.getAccountId().toString(), event);
            log.info("Successfully sent notification event for account ID: {}", event.getAccountId());
            
        } catch (Exception e) {
            log.error("Failed to send notification event for account ID: {}", event.getAccountId(), e);
        }
    }
}