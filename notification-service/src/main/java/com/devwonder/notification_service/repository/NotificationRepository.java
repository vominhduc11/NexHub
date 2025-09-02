package com.devwonder.notification_service.repository;

import com.devwonder.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Repository được clean - chỉ sử dụng findAll() từ JpaRepository
    // Các methods khác đã được xóa vì chỉ cần API /all
}