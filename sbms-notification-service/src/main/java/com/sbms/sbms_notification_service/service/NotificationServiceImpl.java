//package com.sbms.sbms_notification_service.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.sbms.sbms_notification_service.model.Notification;
//import com.sbms.sbms_notification_service.repository.NotificationRepository;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//public class NotificationServiceImpl implements NotificationService {
//
//    private final NotificationRepository repo;
//
//    public NotificationServiceImpl(NotificationRepository repo) {
//        this.repo = repo;
//    }
//
//    @Override
//    @Transactional
//    public Notification create(Notification notification) {
//        if (notification.getNotificationId() == null) {
//            notification.setNotificationId(UUID.randomUUID().toString());
//        }
//        notification.setCreatedAt(Instant.now());
//        notification.setRead(false);
//        return repo.save(notification);
//    }
//
//    @Override
//    public List<Notification> getNotificationsForUser(String userId) {
//        return repo.findByUserIdOrderByCreatedAtDesc(userId);
//    }
//
//    @Override
//    public long getUnreadCount(String userId) {
//        return repo.countByUserIdAndRead(userId, false);
//    }
//
//    @Override
//    @Transactional
//    public Notification markAsRead(String notificationId) {
//        var opt = repo.findById(notificationId);
//        if (opt.isEmpty()) return null;
//        var n = opt.get();
//        n.setRead(true);
//        return repo.save(n);
//    }
//
//    @Override
//    @Transactional
//    public void markAllAsRead(String userId) {
//        var list = repo.findByUserIdAndReadOrderByCreatedAtDesc(userId, false);
//        list.forEach(n -> n.setRead(true));
//        repo.saveAll(list);
//    }
//}









package com.sbms.sbms_notification_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbms.sbms_notification_service.model.Notification;
import com.sbms.sbms_notification_service.repository.NotificationRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;

    public NotificationServiceImpl(NotificationRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public Notification create(Notification notification) {
        if (notification.getNotificationId() == null) {
            notification.setNotificationId(UUID.randomUUID().toString());
        }
        notification.setCreatedAt(Instant.now());
        notification.setRead(false);
        return repo.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(String userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public long getUnreadCount(String userId) {
        return repo.countByUserIdAndRead(userId, false);
    }

    @Override
    @Transactional
    public Notification markAsRead(String notificationId) {
        var opt = repo.findById(notificationId);
        if (opt.isEmpty()) return null;
        var n = opt.get();
        n.setRead(true);
        return repo.save(n);
    }

    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        var list = repo.findByUserIdAndReadOrderByCreatedAtDesc(userId, false);
        list.forEach(n -> n.setRead(true));
        repo.saveAll(list);
    }
}


