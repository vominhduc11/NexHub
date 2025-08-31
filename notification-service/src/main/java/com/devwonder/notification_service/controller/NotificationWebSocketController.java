package com.devwonder.notification_service.controller;

import com.devwonder.notification_service.dto.DealerNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class NotificationWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketController.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
        
        messagingTemplate.convertAndSendToUser(username, "/queue/private", new DealerNotification(
            notificationType,
            username,
            "", // name not needed for private notification
            "",
            message,
            System.currentTimeMillis()
        ));
        
        log.info("Private notification sent to user: {}", username);
    }
    
    // Send broadcast notification to all users
    public void broadcastToAllUsers(String message) {
        log.info("Broadcasting to all users: {}", message);
        
        messagingTemplate.convertAndSend("/topic/notifications", new DealerNotification(
            "BROADCAST_NOTIFICATION",
            "system",
            "System",
            "",
            message,
            System.currentTimeMillis()
        ));
        
        log.info("Broadcast notification sent to all users");
    }

}