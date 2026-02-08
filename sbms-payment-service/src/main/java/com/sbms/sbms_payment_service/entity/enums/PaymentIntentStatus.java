package com.sbms.sbms_payment_service.entity.enums;

public enum PaymentIntentStatus {
	CREATED,
    PROCESSING,
    SUCCESS,
    FAILED,
    EXPIRED,
    PENDING_VERIFICATION,    // slip uploaded
    PENDING_CASH_CONFIRMATION
}
