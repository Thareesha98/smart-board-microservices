package com.sbms.sbms_backend.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSucceededEvent {
    private String eventId;
    private Long intentId;
    private Long transactionId;
    private Long studentId;
    private Long ownerId;
    private Long monthlyBillId;
    private BigDecimal amount;
    private String method;
    private LocalDateTime occurredAt;
}
