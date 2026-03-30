package com.sbms.sbms_payment_service.events;




import lombok.Data;

@Data
public class NotificationFailedEvent {
    private Long intentId;
    private Long transactionId;
    private String userId;
    private String reason;
}
