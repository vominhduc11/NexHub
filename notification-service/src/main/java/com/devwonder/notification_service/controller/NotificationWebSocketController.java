package com.devwonder.notification_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastDealerRegistration(String dealerUsername, String dealerName, String dealerEmail) {
        log.info("Broadcasting dealer registration notification for: {}", dealerUsername);
        
        String message = String.format("New dealer registered: %s (%s)", dealerName, dealerUsername);
        
        messagingTemplate.convertAndSend("/topic/dealer-registrations", new DealerNotification(
            "DEALER_REGISTERED",
            dealerUsername,
            dealerName,
            dealerEmail,
            message,
            System.currentTimeMillis()
        ));
        
        log.info("Dealer registration notification sent via WebSocket for: {}", dealerUsername);
    }

    // Send private notification to specific user
    public void sendPrivateNotification(String username, String notificationType, String message) {
        log.info("Sending private notification to user: {}", username);
        
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", new DealerNotification(
            notificationType,
            username,
            "", // name not needed for private notification
            "",
            message,
            System.currentTimeMillis()
        ));
        
        log.info("Private notification sent to user: {}", username);
    }

    public static class DealerNotification {
        private String type;
        private String username;
        private String name;
        private String email;
        private String message;
        private long timestamp;

        public DealerNotification(String type, String username, String name, String email, String message, long timestamp) {
            this.type = type;
            this.username = username;
            this.name = name;
            this.email = email;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getType() {
            return type;
        }

        public String getUsername() {
            return username;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}