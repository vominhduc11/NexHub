package com.devwonder.notification_service.controller;

import com.devwonder.common.exception.BaseException;
import com.devwonder.notification_service.dto.DealerNotification;
import com.devwonder.notification_service.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastDealerRegistration(Notification savedNotification) throws BaseException {
        log.info("Broadcasting dealer registration notification: {} (ID: {})", savedNotification.getTitle(), savedNotification.getId());
        
        // Send the actual saved notification record to WebSocket
        messagingTemplate.convertAndSend("/topic/dealer-registrations", savedNotification);
        
        log.info("Dealer registration notification sent via WebSocket (ID: {})", savedNotification.getId());
    }

    // Send private notification to specific user
    public void sendPrivateNotification(String username, String notificationType, String message) throws BaseException {
        log.info("Sending private notification to user: {}", username);
        
        DealerNotification notification = new DealerNotification(
            notificationType,
            username,
            "", // name not needed for private notification
            "",
            message,
            System.currentTimeMillis()
        );
        
        log.info("Message details - User: {}, Type: {}, Message: {}", username, notificationType, message);
        log.info("Full destination will be: /user/{}/queue/private", username);
        
        messagingTemplate.convertAndSendToUser(username, "/queue/private", notification);
        
        log.info("Private notification sent successfully to user: {} at destination: /user/{}/queue/private", username, username);
    }
    
    // Send broadcast notification to all users
    public void broadcastToAllUsers(String message) throws BaseException {
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
    
    // Send broadcast notification to ADMIN users only
    public void sendBroadcastNotification(String notificationType, String message) throws BaseException {
        log.info("Broadcasting notification to ADMIN users - Type: {}, Message: {}", notificationType, message);
        
        messagingTemplate.convertAndSend("/topic/dealer-registrations", new DealerNotification(
            notificationType,
            "system",
            "System",
            "",
            message,
            System.currentTimeMillis()
        ));
        
        log.info("Notification sent to ADMIN users successfully - Type: {}", notificationType);
    }

}