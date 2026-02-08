package com.sbms.sbms_payment_service.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReceiptDataDTO(
        String receiptNo,
        LocalDateTime paidAt,
        String paymentMethod,
        String studentName,
        String ownerName,
        String boardingTitle,
        BigDecimal grossAmount,
        BigDecimal platformFee,
        BigDecimal gatewayFee,
        BigDecimal netAmount
) {}
