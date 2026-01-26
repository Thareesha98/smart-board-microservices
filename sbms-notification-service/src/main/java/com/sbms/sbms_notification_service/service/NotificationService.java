//package com.sbms.sbms_notification_service.service;
//
//
//import java.util.List;
//
//import com.sbms.sbms_notification_service.model.Notification;
//
//public interface NotificationService {
//    Notification create(Notification notification);
//    List<Notification> getNotificationsForUser(String userId);
//    long getUnreadCount(String userId);
//    Notification markAsRead(String notificationId);
//    void markAllAsRead(String userId);
//}



package com.sbms.sbms_notification_service.service;

import java.util.List;
import com.sbms.sbms_notification_service.model.Notification;

public interface NotificationService {

    Notification create(Notification notification);
    List<Notification> getNotificationsForUser(String userId);
    long getUnreadCount(String userId);
    Notification markAsRead(String notificationId);
    void markAllAsRead(String userId);
}
