package com.devwonder.notification_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationWebSocketController webSocketController;



    @MessageMapping("/broadcast")
    public void broadcastToAll(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "Anonymous";
            log.info("ADMIN {} broadcasting to all users: {}", username, message);
            
            String broadcastMessage = String.format("[Broadcast from ADMIN %s]: %s", username, message);
            webSocketController.broadcastToAllUsers(broadcastMessage);
            
        } catch (Exception e) {
            log.error("Broadcast failed: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/private/{targetUser}")
    public void sendPrivateNotification(
            @DestinationVariable String targetUser,
            @Payload String message,
            SimpMessageHeaderAccessor headerAccessor) {
        
        try {
            String senderUsername = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "Anonymous";
            log.info("ADMIN {} sending private notification to CUSTOMER {}: {}", senderUsername, targetUser, message);
            
            String privateMessage = String.format("[Private from ADMIN %s]: %s", senderUsername, message);
            webSocketController.sendPrivateNotification(targetUser, "PRIVATE_MESSAGE", privateMessage);
            
        } catch (Exception e) {
            log.error("Private message failed: {}", e.getMessage(), e);
        }
    }
}