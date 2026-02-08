package com.sbms.sbms_payment_service.gateway;

import com.sbms.sbms_payment_service.dto.GatewayInitiationResult;
import com.sbms.sbms_payment_service.entity.Payment;
import java.util.concurrent.CompletableFuture;

public interface PaymentGatewayClient {

    CompletableFuture<GatewayInitiationResult> initiate(Payment payment);
}
