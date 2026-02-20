package com.sbms.sbms_payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResult {

    private Long paymentId;
    private String transactionId;
    private Double amount;
    private String status;
    private String receiptUrl;
}
