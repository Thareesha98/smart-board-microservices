package com.sbms.sbms_payment_service.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.sbms.sbms_payment_service.entity.Payment;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.enums.PaymentStatus;
import com.sbms.sbms_payment_service.events.PaymentSucceededEvent;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;
import com.sbms.sbms_payment_service.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class WebhookFinalizationService {

    private final PaymentRepository paymentRepository;
    private final PaymentIntentRepository intentRepository;
    private final PaymentIntentService intentService;
    private final OwnerWalletCreditService walletCreditService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void finalizeSuccess(
            String gateway,
            String gatewayReference
    ) {
        Payment payment = paymentRepository
                .findByGatewayAndGatewayReference(gateway, gatewayReference)
                .orElseThrow(() -> new IllegalStateException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return; // idempotent webhook
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        PaymentIntent intent =
                intentRepository.findById(payment.getPaymentIntentId())
                        .orElseThrow();

        intentService.markSuccess(intent);

        walletCreditService.credit(
                intent.getOwnerId(),
                payment.getAmount(),
                payment.getId().toString()
        );
        
        
        eventPublisher.publishEvent(
                new PaymentSucceededEvent(
                        payment.getId(),
                        intent.getId(),
                        intent.getStudentId(),
                        intent.getOwnerId(),
                        intent.getBoardingId(),
                        intent.getReferenceType(),
                        intent.getReferenceId()
                )
        );
    }

    @Transactional
    public void finalizeFailure(
            String gateway,
            String gatewayReference,
            String reason
    ) {
        Payment payment = paymentRepository
                .findByGatewayAndGatewayReference(gateway, gatewayReference)
                .orElseThrow();

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        paymentRepository.save(payment);

        PaymentIntent intent =
                intentRepository.findById(payment.getPaymentIntentId())
                        .orElseThrow();

        intentService.markFailed(intent, reason);
    }
}
