package com.sbms.sbms_notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sbms.sbms_notification_service.model.NotificationToken;

import java.util.Optional;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

    Optional<NotificationToken> findByUserId(String userId);
}
