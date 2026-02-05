package com.sbms.sbms_backend.service;


import com.sbms.sbms_backend.dto.payment.GatewayChargeResult;
import com.sbms.sbms_backend.model.PaymentIntent;
import com.sbms.sbms_backend.model.enums.PaymentMethod;

public interface PaymentGateway {

    GatewayChargeResult charge(
            PaymentIntent intent,
            PaymentMethod method
    );
}
