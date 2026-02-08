package com.sbms.sbms_payment_service.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        Long paymentId,
        Long paymentIntentId,
        String status,
        BigDecimal amount
) {}
