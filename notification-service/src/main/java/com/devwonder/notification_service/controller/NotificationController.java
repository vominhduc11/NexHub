package com.devwonder.notification_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification Management", description = "Operations related to notification management")
public class NotificationController {

    @GetMapping("/health")
    @Operation(summary = "Check notification service health", description = "Simple health check endpoint")
    public String health() {
        return "Notification service is running!";
    }
}