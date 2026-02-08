package com.sbms.sbms_payment_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sbms.sbms_payment_service.entity.Payment;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.enums.PaymentMethod;
import com.sbms.sbms_payment_service.entity.enums.PaymentStatus;
import com.sbms.sbms_payment_service.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentExecutionService {

    private final PaymentRepository paymentRepository;
    private final PaymentIntentService intentService;

    @Transactional
    public Payment createPaymentAttempt(
            PaymentIntent intent,
            String idempotencyKey,
            String gateway
    ) {

        // Idempotency at execution layer
        return paymentRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElseGet(() -> {

                    intentService.markProcessing(intent);

                    Payment payment = new Payment();
                    payment.setPaymentIntentId(intent.getId());
                    payment.setIdempotencyKey(idempotencyKey);
                    payment.setGateway(gateway);
                    payment.setAmount(intent.getAmount());
                    payment.setStatus(PaymentStatus.PROCESSING);

                    return paymentRepository.save(payment);
                });
    }
    
    
    @Transactional
    public Payment initiateManualPayment(
            PaymentIntent intent,
            String idempotencyKey,
            PaymentMethod method,
            String slipUrl // nullable for CASH
    ) {
        Payment payment = paymentRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElseGet(() -> {

                    intentService.markProcessing(intent);

                    Payment p = new Payment();
                    p.setPaymentIntentId(intent.getId());
                    p.setIdempotencyKey(idempotencyKey);
                    p.setGateway("MANUAL");
                    p.setAmount(intent.getAmount());

                    if (method == PaymentMethod.BANK_TRANSFER) {
                        p.setStatus(PaymentStatus.PENDING_VERIFICATION);
                    } else {
                        p.setStatus(PaymentStatus.PENDING_CASH_CONFIRMATION);
                    }

                    return paymentRepository.save(p);
                });

        return payment;
    }

}
