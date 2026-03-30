package com.sbms.sbms_payment_service.service;





import lombok.Data;

@Data
public class PaymentRollbackEvent {
    private Long intentId;
    private Long transactionId;
    private String reason;
}