package com.sbms.sbms_notification_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_notification_service.model.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Notification> findByUserIdAndReadOrderByCreatedAtDesc(String userId, boolean read);
    long countByUserIdAndRead(String userId, boolean read);
}
