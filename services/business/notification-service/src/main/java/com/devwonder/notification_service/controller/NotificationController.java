package com.devwonder.notification_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.notification_service.entity.Notification;
import com.devwonder.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get all notifications
     */
    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<Notification>>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(BaseResponse.success("Notifications retrieved successfully", notifications));
    }

    /**
     * Update notification read status
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<BaseResponse<Notification>> markNotificationAsRead(@PathVariable Long id) {
        Notification updatedNotification = notificationService.markAsRead(id);
        return ResponseEntity.ok(BaseResponse.success("Notification marked as read successfully", updatedNotification));
    }
}