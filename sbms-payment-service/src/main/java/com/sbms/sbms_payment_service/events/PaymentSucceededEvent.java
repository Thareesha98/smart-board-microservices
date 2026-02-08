package com.sbms.sbms_payment_service.events;


public record PaymentSucceededEvent(
        Long paymentId,
        Long paymentIntentId,
        Long studentId,
        Long ownerId,
        Long boardingId,
        String referenceType,
        String referenceId
) {}
