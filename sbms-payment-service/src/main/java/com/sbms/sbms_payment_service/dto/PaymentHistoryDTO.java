package com.sbms.sbms_payment_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sbms.sbms_notification_service.model.enums.PaymentMethod;
import com.sbms.sbms_notification_service.model.enums.PaymentStatus;

@Data
public class PaymentHistoryDTO {

    private Long id;

    private String transactionRef;

    private BigDecimal amount;

    private PaymentMethod method;

    private PaymentStatus status;

    private String failureReason;

    private LocalDateTime paidAt;
    
    private String receiptUrl;
    
    private BigDecimal platformFee;
    private BigDecimal gatewayFee;
    private BigDecimal netAmount;


}
