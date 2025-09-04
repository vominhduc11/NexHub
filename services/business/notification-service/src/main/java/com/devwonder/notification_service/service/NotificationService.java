package com.devwonder.notification_service.service;

import com.devwonder.notification_service.entity.Notification;
import com.devwonder.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Create and save a new notification
     */
    @Transactional
    public Notification createNotification(String title, String message, String type) {
        String currentTime = LocalDateTime.now().format(FORMATTER);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTime(currentTime);
        notification.setType(type);
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);
        log.info("✅ Created notification: ID={}, Type={}, Title={}", saved.getId(), type, title);

        return saved;
    }

    /**
     * Create dealer registration notification
     */
    @Transactional
    public Notification createDealerRegistrationNotification(String dealerName, String username) {
        String title = "Đăng ký đại lý mới";
        String message = String.format(
                "Đại lý '%s' (username: %s) vừa đăng ký thành công. Vui lòng xem xét và phê duyệt.",
                dealerName, username);

        String currentTime = LocalDateTime.now().format(FORMATTER);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTime(currentTime);
        notification.setType("DEALER_REGISTRATION");
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);
        log.info("✅ Created dealer registration notification: ID={}, Dealer={}", saved.getId(), dealerName);

        return saved;
    }

    /**
     * Get all notifications ordered by newest first (descending by creation time)
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        
        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        
        log.info("✅ Marked notification as read: ID={}", id);
        return updated;
    }
}