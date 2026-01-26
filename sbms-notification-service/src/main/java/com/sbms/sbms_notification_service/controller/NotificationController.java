//package com.sbms.sbms_notification_service.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.sbms.sbms_notification_service.model.Notification;
//import com.sbms.sbms_notification_service.service.NotificationService;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/notifications")
//public class NotificationController {
//
//    private final NotificationService service;
//
//    public NotificationController(NotificationService service) {
//        this.service = service;
//    }
//
//    private String getEmail(HttpServletRequest request) {
//        String email = request.getHeader("X-User-Email");
//        System.out.println("🔍 X-User-Email received = [" + email + "]");
//        if (email == null || email.isBlank()) {
//            throw new RuntimeException("Unauthorized: Missing X-User-Email header from gateway");
//        }
//        return email;
//    }
//
//
//    // GET /api/notifications/unread-count
//    @GetMapping("/unread-count")
//    public ResponseEntity<Long> unreadCount(HttpServletRequest request) {
//        String email = getEmail(request);
//        long count = service.getUnreadCount(email);
//        return ResponseEntity.ok(count);
//    }
//
//    // GET /api/notifications
//    @GetMapping
//    public ResponseEntity<List<Notification>> getNotifications(HttpServletRequest request) {
//        String email = getEmail(request);
//        return ResponseEntity.ok(service.getNotificationsForUser(email));
//    }
//
//    // PUT /api/notifications/{id}/read
//    @PutMapping("/{id}/read")
//    public ResponseEntity<Notification> markRead(@PathVariable("id") String id) {
//        var n = service.markAsRead(id);
//        if (n == null) return ResponseEntity.notFound().build();
//        return ResponseEntity.ok(n);
//    }
//
//    // PUT /api/notifications/read-all
//    @PutMapping("/read-all")
//    public ResponseEntity<Void> markAllRead(HttpServletRequest request) {
//        String email = getEmail(request);
//        service.markAllAsRead(email);
//        return ResponseEntity.noContent().build();
//    }
//}












package com.sbms.sbms_notification_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_notification_service.dto.ExpoTokenRequest;
import com.sbms.sbms_notification_service.model.Notification;
import com.sbms.sbms_notification_service.service.ExpoPushService;
import com.sbms.sbms_notification_service.service.NotificationService;
import com.sbms.sbms_notification_service.service.NotificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationService service;
    private final NotificationTokenService tokenService;
    private final ExpoPushService pushService;

    public NotificationController(
            NotificationService service,
            NotificationTokenService tokenService,
            ExpoPushService pushService
    ) {
        this.service = service;
        this.tokenService = tokenService;
        this.pushService = pushService;
    }
    
    
    
    private String getUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        System.out.println("🔍 X-User-Id received = [" + userId + "]");

        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("Unauthorized: Missing X-User-Id header from gateway");
        }
        return userId;
    }
    
    
 // -------------------------------
    // Register Expo Push Token
    // -------------------------------
    @PostMapping("/register-token")
    public ResponseEntity<Void> registerToken(
            @RequestBody ExpoTokenRequest req,
            HttpServletRequest request
    ) {
        String userId = getUserId(request);
        tokenService.save(userId, req.getExpoToken());
        System.out.println("✅ Expo push token saved for userId=" + userId);
        return ResponseEntity.ok().build();
    }
    
    

    // GET /notifications/unread-count
    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(HttpServletRequest request) {
        String userId = getUserId(request);
        long count = service.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    // GET /notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(HttpServletRequest request) {
        String userId = getUserId(request);
        return ResponseEntity.ok(service.getNotificationsForUser(userId));
    }

    // PUT /notifications/{id}/read
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markRead(@PathVariable("id") String id) {
        var n = service.markAsRead(id);
        if (n == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(n);
    }

    // PUT /notifications/read-all
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllRead(HttpServletRequest request) {
        String userId = getUserId(request);
        service.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}













