package com.sbms.sbms_backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sbms.sbms_backend.dto.payment.GatewayChargeResult;
import com.sbms.sbms_backend.model.PaymentIntent;
import com.sbms.sbms_backend.model.enums.PaymentMethod;

@Service
public class DummyPayHereGateway implements PaymentGateway {

    @Override
    public GatewayChargeResult charge(
            PaymentIntent intent,
            PaymentMethod method
    ) {
        return new GatewayChargeResult(
                true,
                "PH-" + UUID.randomUUID(),
                "Payment successful (Dummy PayHere)"
        );
    }
}
