package com.sbms.sbms_payment_service.service;

import com.sbms.sbms_notification_service.model.enums.PaymentMethod;
import com.sbms.sbms_payment_service.dto.GatewayChargeResult;
import com.sbms.sbms_payment_service.entity.PaymentIntent;

public interface PaymentGateway {

    GatewayChargeResult charge(
            PaymentIntent intent,
            PaymentMethod method
    );
}
