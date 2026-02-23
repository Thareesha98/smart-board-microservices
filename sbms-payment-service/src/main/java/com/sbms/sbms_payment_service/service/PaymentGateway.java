package com.sbms.sbms_payment_service.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

import com.sbms.sbms_payment_service.dto.GatewayChargeResult;
import com.sbms.sbms_payment_service.entity.PaymentIntent;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentGateway {
	
	  /**
     * Fallback when:
     * - Gateway timeout
     * - Network failure
     * - Circuit open
     */

    /**
     * PRODUCTION NOTES:
     * - No TimeLimiter (sync method)
     * - CircuitBreaker + Retry = correct pattern for payment gateways
     */
    
	@CircuitBreaker(name = "payhere", fallbackMethod = "fallbackCharge")
	@Retry(name = "payhere")
	public GatewayChargeResult charge(PaymentIntent intent, Object method) {

	    try {
	        log.info("Charging payment via gateway for intent={}", intent.getId());

	        // Simulated external call
	        return new GatewayChargeResult(
	                true,
	                "PH-" + intent.getId(),
	                "SUCCESS"
	        );

	    } catch (Exception ex) {
	        log.error("Gateway internal error for intent {}", intent.getId(), ex);
	        throw new RuntimeException("Gateway processing failed", ex);
	    }
	}
    /**
     * Fallback when:
     * - Gateway timeout
     * - Network failure
     * - Circuit open
     */
    public GatewayChargeResult fallbackCharge(
            PaymentIntent intent,
            Object method,
            Throwable ex) {

        log.error("Gateway FAILED for intent {}. Triggering fallback.", intent.getId(), ex);

        return new GatewayChargeResult(
                false,
                "FAILED-" + intent.getId(),
                "FAILED"
        );
    }
}