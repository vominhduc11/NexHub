package com.devwonder.notification_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "WebSocket notification for dealer-related events")
public class DealerNotification {
    
    @Schema(description = "Type of notification", example = "DEALER_REGISTERED")
    private String type;
    
    @Schema(description = "Username of the dealer", example = "dealer001")
    private String username;
    
    @Schema(description = "Full name of the dealer", example = "John Doe")
    private String name;
    
    @Schema(description = "Email address of the dealer", example = "dealer001@example.com")
    private String email;
    
    @Schema(description = "Notification message content", example = "New dealer registered: John Doe (dealer001)")
    private String message;
    
    @Schema(description = "Timestamp when notification was created", example = "1693123456789")
    private long timestamp;
}