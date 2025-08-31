package com.devwonder.notification_service.controller;

import com.devwonder.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Management", description = "Real-time notification and WebSocket communication endpoints")
@SecurityRequirement(name = "Gateway Request")
public class NotificationController {

    private final NotificationWebSocketController webSocketController;

    @GetMapping("/health") 
    @Operation(summary = "Health check", description = "Check if notification service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<BaseResponse<String>> health() {
        try {
            return ResponseEntity.ok(BaseResponse.success("OK", "Notification service is running"));
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(BaseResponse.error("Health check failed", "HEALTH_CHECK_ERROR", e.getMessage()));
        }
    }

    @PostMapping("/broadcast")
    @Operation(summary = "Broadcast notification to all users", description = "Send notification to all connected users via WebSocket")
    @ApiResponse(responseCode = "200", description = "Broadcast notification sent successfully")
    public ResponseEntity<BaseResponse<String>> broadcastToAll(
            @Parameter(description = "Broadcast message") @RequestBody String message) {
        
        try {
            log.info("Broadcasting to all users: {}", message);
            webSocketController.broadcastToAllUsers(message);
            
            return ResponseEntity.ok(BaseResponse.success(
                "Sent to all connected users",
                "Broadcast notification sent"
            ));
        } catch (Exception e) {
            log.error("Broadcast failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(BaseResponse.error("Broadcast failed", "BROADCAST_ERROR", e.getMessage()));
        }
    }

    @PostMapping("/user/{username}/send")
    @Operation(summary = "Send private notification", description = "Send private notification to specific user via WebSocket")
    @ApiResponse(responseCode = "200", description = "Private notification sent successfully")
    public ResponseEntity<BaseResponse<String>> sendPrivateNotification(
            @Parameter(description = "Target username") @PathVariable String username,
            @Parameter(description = "Private notification message") @RequestBody String message) {
        
        log.info("Sending private notification to {}: {}", username, message);
        webSocketController.sendPrivateNotification(username, "PRIVATE_MESSAGE", message);
        
        return ResponseEntity.ok(BaseResponse.success(
            "Private notification sent",
            "Sent to user: " + username
        ));
    }
}