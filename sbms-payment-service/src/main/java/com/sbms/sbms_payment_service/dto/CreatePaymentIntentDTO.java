package com.sbms.sbms_payment_service.dto;


import lombok.Data;

import java.math.BigDecimal;

import com.sbms.sbms_notification_service.model.enums.PaymentType;

@Data
public class CreatePaymentIntentDTO {

    private Long studentId;
    private Long ownerId;
    private Long boardingId;

    private PaymentType type; // KEY_MONEY, MONTHLY_RENT, UTILITIES

    private BigDecimal amount;

    // Optional (used for rent/utilities)
    private Long monthlyBillId;

    private String description; // "January Rent", "Key Money", etc.
}
