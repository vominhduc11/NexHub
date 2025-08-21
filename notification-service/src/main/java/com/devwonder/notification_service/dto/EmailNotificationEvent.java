package com.devwonder.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationEvent {
    
    private String eventId;
    private String eventType;
    private String recipientEmail;
    private String recipientName;
    private String subject;
    private String templateName;
    private Map<String, Object> templateData;
    private String priority;
    private LocalDateTime createdAt;
    private String sourceService;
    private Long userId;
    private String userRole;
    
    // Business logic constructors
    public EmailNotificationEvent(String eventType, String recipientEmail, String subject, 
                                String templateName, Map<String, Object> templateData) {
        this.eventType = eventType;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.templateName = templateName;
        this.templateData = templateData;
        this.priority = "NORMAL";
        this.createdAt = LocalDateTime.now();
    }
    
    public EmailNotificationEvent(String eventType, String recipientEmail, String recipientName,
                                String subject, String templateName, Map<String, Object> templateData,
                                String sourceService, Long userId, String userRole) {
        this.eventType = eventType;
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.subject = subject;
        this.templateName = templateName;
        this.templateData = templateData;
        this.priority = "NORMAL";
        this.createdAt = LocalDateTime.now();
        this.sourceService = sourceService;
        this.userId = userId;
        this.userRole = userRole;
    }
}