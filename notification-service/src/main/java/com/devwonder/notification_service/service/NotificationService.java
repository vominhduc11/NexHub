package com.devwonder.notification_service.service;

import com.devwonder.notification_service.entity.Notification;
import com.devwonder.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

        return createNotification(title, message, "DEALER_REGISTRATION");
    }

    /**
     * Get all notifications
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findLatestNotifications();
    }

    /**
     * Get notifications with pagination
     */
    public List<Notification> getNotifications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findTopNotifications(pageable);
    }

    /**
     * Get unread notifications
     */
    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByReadFalse();
    }

    /**
     * Get notifications by type
     */
    public List<Notification> getNotificationsByType(String type) {
        return notificationRepository.findByType(type);
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public boolean markAsRead(Long id) {
        try {
            notificationRepository.markAsRead(id);
            log.info("✅ Marked notification {} as read", id);
            return true;
        } catch (Exception e) {
            log.error("❌ Failed to mark notification {} as read", id, e);
            return false;
        }
    }

    /**
     * Mark all notifications as read
     */
    @Transactional
    public void markAllAsRead() {
        notificationRepository.markAllAsRead();
        log.info("✅ Marked all notifications as read");
    }

    /**
     * Mark all notifications of specific type as read
     */
    @Transactional
    public void markAllAsReadByType(String type) {
        notificationRepository.markAllAsReadByType(type);
        log.info("✅ Marked all notifications of type '{}' as read", type);
    }

    /**
     * Get notification by ID
     */
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    /**
     * Delete notification
     */
    @Transactional
    public boolean deleteNotification(Long id) {
        try {
            notificationRepository.deleteById(id);
            log.info("✅ Deleted notification with ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("❌ Failed to delete notification with ID: {}", id, e);
            return false;
        }
    }

    /**
     * Get notification counts
     */
    public long getUnreadCount() {
        return notificationRepository.countByReadFalse();
    }

    public long getCountByType(String type) {
        return notificationRepository.countByType(type);
    }
}