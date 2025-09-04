package com.devwonder.notification_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dealer notification data transfer object")
public class DealerNotification {
    
    @Schema(description = "Notification type", example = "DEALER_UPDATE")
    private String type;
    
    @Schema(description = "Dealer username", example = "dealer123")
    private String username;
    
    @Schema(description = "Dealer full name", example = "John Doe")
    private String name;
    
    @Schema(description = "Dealer email address", example = "dealer@example.com")
    private String email;
    
    @Schema(description = "Notification message content", example = "New product available")
    private String message;
    
    @Schema(description = "Timestamp when notification was created", example = "1672531200000")
    private long timestamp;
}