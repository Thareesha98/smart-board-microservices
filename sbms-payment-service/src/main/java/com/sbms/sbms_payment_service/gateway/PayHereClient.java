package com.sbms.sbms_payment_service.gateway;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import com.sbms.sbms_payment_service.dto.GatewayInitiationResult;
import com.sbms.sbms_payment_service.entity.Payment;


@Component
@RequiredArgsConstructor
public class PayHereClient implements PaymentGatewayClient {

    @Override
    @CircuitBreaker(name = "payhere")
    @Retry(name = "payhere")
    @Bulkhead(name = "payhere", type = Bulkhead.Type.SEMAPHORE)
    @TimeLimiter(name = "payhere")
    public CompletableFuture<GatewayInitiationResult> initiate(Payment payment) {

        return CompletableFuture.supplyAsync(() -> {

            // Simulated external call
            // DO NOT touch DB here

            return new GatewayInitiationResult(
                    "PH-" + payment.getId(),
                    "https://payhere.lk/redirect/" + payment.getId()
            );
        });
    }
}
