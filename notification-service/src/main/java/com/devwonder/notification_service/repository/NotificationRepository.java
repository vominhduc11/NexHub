package com.devwonder.notification_service.repository;

import com.devwonder.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications by type
     */
    List<Notification> findByType(String type);
    
    /**
     * Find all unread notifications
     */
    List<Notification> findByReadFalse();
    
    /**
     * Find all read notifications
     */
    List<Notification> findByReadTrue();
    
    /**
     * Find notifications by type and read status
     */
    List<Notification> findByTypeAndRead(String type, Boolean read);
    
    /**
     * Count unread notifications
     */
    long countByReadFalse();
    
    /**
     * Count notifications by type
     */
    long countByType(String type);
    
    /**
     * Mark notification as read
     */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);
    
    /**
     * Mark all notifications as read
     */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.read = false")
    void markAllAsRead();
    
    /**
     * Mark all notifications of specific type as read
     */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.type = :type AND n.read = false")
    void markAllAsReadByType(@Param("type") String type);
    
    /**
     * Find latest notifications (ordered by creation time)
     */
    @Query("SELECT n FROM Notification n ORDER BY n.createdAt DESC")
    List<Notification> findLatestNotifications();
    
    /**
     * Find notifications with pagination
     */
    @Query("SELECT n FROM Notification n ORDER BY n.createdAt DESC")
    List<Notification> findTopNotifications(org.springframework.data.domain.Pageable pageable);
}