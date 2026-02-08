package com.sbms.sbms_payment_service.events;

public record PaymentFailedEvent(
        Long paymentId,
        Long paymentIntentId,
        String reason
) {}
