package com.sbms.sbms_notification_service.dto;



import lombok.Data;

@Data
public class NotificationFailedEvent {
    private Long intentId;
    private Long transactionId;
    private String userId;
    private String reason;
}
