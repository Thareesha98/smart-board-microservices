package com.sbms.sbms_notification_service.listener;

import com.sbms.sbms_notification_service.dto.EventMessageDto;
import com.sbms.sbms_notification_service.model.Notification;
import com.sbms.sbms_notification_service.model.NotificationType;
import com.sbms.sbms_notification_service.service.ExpoPushService;
import com.sbms.sbms_notification_service.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final ExpoPushService expoPushService;
    private final ObjectMapper mapper;

    public NotificationEventListener(
            NotificationService notificationService,
            ExpoPushService expoPushService,
            ObjectMapper mapper
    ) {
        this.notificationService = notificationService;
        this.expoPushService = expoPushService;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "${rabbitmq.queue:sbms.notification.queue}")
    public void onEvent(@Payload EventMessageDto event) {
        try {
            if (event == null) return;

            String userId = resolveUserId(event);
            if (userId == null) return;

            String title = buildTitle(event.eventType());
            String message = buildMessage(event.eventType(), event.data());
            NotificationType type = mapType(event.eventType());

            Notification notification = Notification.builder()
                    .notificationId(UUID.randomUUID().toString())
                    .userId(userId)
                    .title(title)
                    .message(message)
                    .type(type)
                    .meta(mapper.writeValueAsString(event.data()))
                    .build();

            // 1️⃣ Save notification
            notificationService.create(notification);

            // 2️⃣ SEND PUSH 🔥 (THIS WAS MISSING)
           expoPushService.sendToUser(userId, title, message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String resolveUserId(EventMessageDto event) {
        if (event.userId() != null && !event.userId().isBlank()) {
            return event.userId();
        }
        if (event.data() != null && event.data().get("userId") != null) {
            return String.valueOf(event.data().get("userId"));
        }
        return null;
    }

    private String buildTitle(String eventType) {
        return switch (eventType.toLowerCase()) {
            case "appointment.created" -> "Appointment requested";
            case "appointment.accepted" -> "Appointment accepted";
            case "appointment.declined" -> "Appointment declined";
            case "appointment.cancelled" -> "Appointment cancelled";
            case "registration.submitted" -> "Registration submitted";
            case "registration.approved" -> "Registration approved";
            case "review.added" -> "New review received";
            case "maintenance.requested" -> "Maintenance request sent";
            
            case "maintenance.updated" -> "Maintenance updated";
            case "maintenance.completed" -> "Maintenance completed";
            case "maintenance.rejected" -> "Maintenance rejected";
            
            
            case "payment.succeeded" -> "Payment Successful";
            case "payment.pending_approval" -> "Payment Submitted";
            case "payment.failed" -> "Payment Failed";

            
            default -> "Notification";
        };
    }

    private String buildMessage(String eventType, Map<String, Object> data) {

        if (eventType == null) return "New notification";

        return switch (eventType.toLowerCase()) {

            case "payment.succeeded" ->
                    "Your payment of LKR " + data.getOrDefault("amount", "0")
                            + " was completed successfully.";

            case "payment.pending_approval" ->
                    "Your payment has been submitted and is awaiting owner approval.";

            case "payment.failed" ->
                    "Your payment has failed. Please try again.";

            default ->
                    eventType + " — " + (data != null ? data.toString() : "");
        };
    }

    private NotificationType mapType(String eventType) {
        return switch (eventType.toLowerCase()) {
            case "appointment.declined", "appointment.cancelled" -> NotificationType.WARNING;
            case "registration.approved", "appointment.accepted" -> NotificationType.SUCCESS;
            default -> NotificationType.INFO;
        };
    }
}
