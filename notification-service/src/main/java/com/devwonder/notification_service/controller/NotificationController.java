package com.devwonder.notification_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.notification_service.dto.DealerNotification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Management", description = "Main notification service endpoints")
@SecurityRequirement(name = "Gateway Request")
public class NotificationController {

    private final NotificationWebSocketController webSocketController;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if notification service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<BaseResponse<String>> health() {
        return ResponseEntity.ok(BaseResponse.success("Notification service is running", "OK"));
    }

    @PostMapping("/admin/broadcast")
    @Operation(summary = "Broadcast admin notification", description = "Send notification to all admin users")
    @ApiResponse(responseCode = "200", description = "Admin notification sent successfully")
    public ResponseEntity<BaseResponse<String>> broadcastAdminNotification(
            @Parameter(description = "Admin notification message") @RequestBody String message) {
        
        log.info("Broadcasting admin notification: {}", message);
        webSocketController.broadcastAdminNotification(message);
        
        return ResponseEntity.ok(BaseResponse.success(
            "Admin notification broadcasted", 
            "Sent to /topic/admin-notifications"
        ));
    }

    @PostMapping("/dealer/broadcast")
    @Operation(summary = "Broadcast dealer update", description = "Send update notification to all dealers")  
    @ApiResponse(responseCode = "200", description = "Dealer update sent successfully")
    public ResponseEntity<BaseResponse<String>> broadcastDealerUpdate(
            @Parameter(description = "Dealer update message") @RequestBody String message) {
        
        log.info("Broadcasting dealer update: {}", message);
        webSocketController.broadcastDealerUpdate(message);
        
        return ResponseEntity.ok(BaseResponse.success(
            "Dealer update broadcasted", 
            "Sent to /topic/dealer-updates"
        ));
    }

    @PostMapping("/user/{username}/send")
    @Operation(summary = "Send private notification", description = "Send private notification to specific user")
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