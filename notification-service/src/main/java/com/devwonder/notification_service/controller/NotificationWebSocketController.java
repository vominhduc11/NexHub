package com.devwonder.notification_service.controller;

import com.devwonder.notification_service.dto.DealerNotification;
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
    
    // Send admin-only notification
    public void broadcastAdminNotification(String message) {
        log.info("Broadcasting admin notification: {}", message);
        
        messagingTemplate.convertAndSend("/topic/admin-notifications", new DealerNotification(
            "ADMIN_NOTIFICATION",
            "system",
            "System Admin",
            "",
            message,
            System.currentTimeMillis()
        ));
        
        log.info("Admin notification broadcasted");
    }
    
    // Send dealer-specific notification
    public void broadcastDealerUpdate(String message) {
        log.info("Broadcasting dealer update: {}", message);
        
        messagingTemplate.convertAndSend("/topic/dealer-updates", new DealerNotification(
            "DEALER_UPDATE",
            "system", 
            "System",
            "",
            message,
            System.currentTimeMillis()
        ));
        
        log.info("Dealer update broadcasted");
    }

}