package com.devwonder.notification_service.controller;

import com.devwonder.notification_service.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification/test")
@RequiredArgsConstructor
@Slf4j
public class NotificationTestController {

    private final NotificationWebSocketController webSocketController;

    @PostMapping("/admin-notification")
    public ResponseEntity<BaseResponse<String>> sendAdminNotification(@RequestParam String message) {
        log.info("Sending admin notification: {}", message);
        
        webSocketController.broadcastAdminNotification(message);
        
        return ResponseEntity.ok(BaseResponse.success("Admin notification sent", "Notification broadcasted to /topic/admin-notifications"));
    }

    @PostMapping("/dealer-update")  
    public ResponseEntity<BaseResponse<String>> sendDealerUpdate(@RequestParam String message) {
        log.info("Sending dealer update: {}", message);
        
        webSocketController.broadcastDealerUpdate(message);
        
        return ResponseEntity.ok(BaseResponse.success("Dealer update sent", "Notification broadcasted to /topic/dealer-updates"));
    }

    @PostMapping("/private-notification")
    public ResponseEntity<BaseResponse<String>> sendPrivateNotification(
            @RequestParam String username,
            @RequestParam String message) {
        log.info("Sending private notification to {}: {}", username, message);
        
        webSocketController.sendPrivateNotification(username, "PRIVATE_MESSAGE", message);
        
        return ResponseEntity.ok(BaseResponse.success("Private notification sent", "Message sent to " + username));
    }
}