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
     * Get unread notifications
     */
    @GetMapping("/unread")
    public ResponseEntity<BaseResponse<List<Notification>>> getUnreadNotifications() {
        List<Notification> notifications = notificationService.getUnreadNotifications();
        return ResponseEntity.ok(BaseResponse.success("Unread notifications retrieved successfully", notifications));
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<BaseResponse<String>> markAsRead(@PathVariable Long id) {
        boolean success = notificationService.markAsRead(id);
        if (success) {
            return ResponseEntity.ok(BaseResponse.success("Notification marked as read successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get notification counts
     */
    @GetMapping("/count/unread")
    public ResponseEntity<BaseResponse<Long>> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(BaseResponse.success("Unread count retrieved successfully", count));
    }
}