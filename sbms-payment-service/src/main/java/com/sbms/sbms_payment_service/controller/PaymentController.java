package com.sbms.sbms_payment_service.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_payment_service.dto.InitiatePaymentRequest;
import com.sbms.sbms_payment_service.dto.PaymentResponse;
import com.sbms.sbms_payment_service.entity.Payment;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.enums.PaymentMethod;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;
import com.sbms.sbms_payment_service.service.IdempotencyService;
import com.sbms.sbms_payment_service.service.PaymentExecutionService;
import com.sbms.sbms_payment_service.service.PaymentVerificationService;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentIntentRepository intentRepository;
    private final PaymentExecutionService executionService;
    private final IdempotencyService idempotencyService;
    private final PaymentVerificationService verificationService;

    @PostMapping("/{intentId}/pay")
    public ResponseEntity<String> initiatePayment(
            @PathVariable Long intentId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody InitiatePaymentRequest request
    ) {
        PaymentIntent intent = intentRepository.findById(intentId)
                .orElseThrow(() -> new IllegalStateException("PaymentIntent not found"));

        return idempotencyService.execute(
                idempotencyKey,
                request,
                () -> {
                    Payment payment = executionService.createPaymentAttempt(
                            intent,
                            idempotencyKey,
                            request.gateway()
                    );

                    PaymentResponse response = new PaymentResponse(
                            payment.getId(),
                            intent.getId(),
                            payment.getStatus().name(),
                            payment.getAmount()
                    );

                    return ResponseEntity.accepted().body(response);
                }
        );
    }
    
    
    @PostMapping("/{intentId}/pay/manual")
    public ResponseEntity<?> manualPay(
            @PathVariable Long intentId,
            @RequestHeader("Idempotency-Key") String key,
            @RequestParam PaymentMethod method,
            @RequestParam(required = false) String slipUrl
    ) {
        PaymentIntent intent = intentRepository.findById(intentId).orElseThrow();

        Payment payment = executionService.initiateManualPayment(
                intent, key, method, slipUrl
        );

        return ResponseEntity.accepted().body(payment.getStatus());
    }
    
    @PostMapping("/verify/{paymentId}/approve")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> approve(
            @PathVariable Long paymentId,
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        verificationService.ownerApprove(paymentId, ownerId);
        return ResponseEntity.ok("APPROVED");
    }


    @PostMapping("/verify/{paymentId}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> reject(
            @PathVariable Long paymentId,
            @RequestHeader("X-User-Id") Long ownerId,
            @RequestParam String reason
    ) {
        verificationService.ownerReject(paymentId, ownerId, reason);
        return ResponseEntity.ok("REJECTED");
    }

    
    


}
