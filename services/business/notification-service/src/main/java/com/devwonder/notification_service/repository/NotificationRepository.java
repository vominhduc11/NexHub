package com.devwonder.notification_service.repository;

import com.devwonder.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications ordered by creation time descending (newest first)
     */
    List<Notification> findAllByOrderByCreatedAtDesc();
}