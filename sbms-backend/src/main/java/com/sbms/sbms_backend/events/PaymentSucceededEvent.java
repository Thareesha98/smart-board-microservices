package com.sbms.sbms_backend.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
