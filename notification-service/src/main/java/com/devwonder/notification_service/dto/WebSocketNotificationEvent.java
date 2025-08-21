package com.devwonder.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketNotificationEvent {
    
    private String eventId;
    private String eventType;
    private String notificationType;
    private String title;
    private String message;
    private Map<String, Object> data;
    private String targetUserId;
    private String targetUserRole;
    private String priority;
    private LocalDateTime createdAt;
    private String sourceService;
    private boolean persistent;
    private Long expiryTime;
    
    // Business logic constructors
    public WebSocketNotificationEvent(String eventType, String notificationType, String title, 
                                    String message, String targetUserId) {
        this.eventType = eventType;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.targetUserId = targetUserId;
        this.priority = "NORMAL";
        this.createdAt = LocalDateTime.now();
        this.persistent = false;
    }
    
    public WebSocketNotificationEvent(String eventType, String notificationType, String title,
                                    String message, Map<String, Object> data, String targetUserId,
                                    String targetUserRole, String sourceService, boolean persistent) {
        this.eventType = eventType;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.data = data;
        this.targetUserId = targetUserId;
        this.targetUserRole = targetUserRole;
        this.priority = "NORMAL";
        this.createdAt = LocalDateTime.now();
        this.sourceService = sourceService;
        this.persistent = persistent;
    }
}