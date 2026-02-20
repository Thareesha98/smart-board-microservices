package com.sbms.sbms_payment_service.events;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;






@Getter
@Setter
public class PaymentSucceededEvent extends BaseEvent {

    private Long intentId;
    private Long transactionId;
    private Long studentId;
    private Long ownerId;
    private Long monthlyBillId;
    private BigDecimal amount;
    private String method;
    private String transactionRef;

    public PaymentSucceededEvent() {
        super("payment.succeeded");
    }
}