package com.devwonder.notification_service.controller;

import com.devwonder.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification/test")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Test", description = "Test endpoints for WebSocket notifications")
public class NotificationTestController {

    private final NotificationWebSocketController webSocketController;

    @PostMapping("/admin-notification")
    @Operation(summary = "Send admin notification", description = "Broadcast a notification to all admin users via WebSocket")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    public ResponseEntity<BaseResponse<String>> sendAdminNotification(
            @Parameter(description = "Notification message to broadcast") @RequestParam String message) {
        log.info("Sending admin notification: {}", message);
        
        webSocketController.broadcastAdminNotification(message);
        
        return ResponseEntity.ok(BaseResponse.success("Admin notification sent", "Notification broadcasted to /topic/admin-notifications"));
    }

    @PostMapping("/dealer-update")
    @Operation(summary = "Send dealer update", description = "Broadcast an update notification to all dealers via WebSocket")
    @ApiResponse(responseCode = "200", description = "Update sent successfully")
    public ResponseEntity<BaseResponse<String>> sendDealerUpdate(
            @Parameter(description = "Update message to broadcast") @RequestParam String message) {
        log.info("Sending dealer update: {}", message);
        
        webSocketController.broadcastDealerUpdate(message);
        
        return ResponseEntity.ok(BaseResponse.success("Dealer update sent", "Notification broadcasted to /topic/dealer-updates"));
    }

    @PostMapping("/private-notification")
    @Operation(summary = "Send private notification", description = "Send a private notification to a specific user via WebSocket")
    @ApiResponse(responseCode = "200", description = "Private notification sent successfully")
    public ResponseEntity<BaseResponse<String>> sendPrivateNotification(
            @Parameter(description = "Username to send notification to") @RequestParam String username,
            @Parameter(description = "Private message content") @RequestParam String message) {
        log.info("Sending private notification to {}: {}", username, message);
        
        webSocketController.sendPrivateNotification(username, "PRIVATE_MESSAGE", message);
        
        return ResponseEntity.ok(BaseResponse.success("Private notification sent", "Message sent to " + username));
    }
}