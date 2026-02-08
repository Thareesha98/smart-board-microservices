package com.sbms.sbms_payment_service.service;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import com.sbms.sbms_payment_service.entity.Payment;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.PaymentVerification;
import com.sbms.sbms_payment_service.entity.enums.PaymentStatus;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;
import com.sbms.sbms_payment_service.repository.PaymentRepository;
import com.sbms.sbms_payment_service.repository.PaymentVerificationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentVerificationService {

    private final PaymentRepository paymentRepository;
    private final PaymentIntentRepository intentRepository;
    private final PaymentVerificationRepository verificationRepository;
    private final PaymentIntentService intentService;
    private final OwnerWalletCreditService walletCreditService;

    @Transactional
    public void ownerApprove(Long paymentId, Long ownerId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow();

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return; // idempotent
        }

        PaymentIntent intent = intentRepository
                .findById(payment.getPaymentIntentId())
                .orElseThrow();

        PaymentVerification v = verificationRepository
                .findByPaymentId(paymentId)
                .orElseThrow();

        v.setApproved(true);
        v.setReviewedByOwnerId(ownerId);
        v.setReviewedAt(OffsetDateTime.now());

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        intentService.markSuccess(intent);

        walletCreditService.credit(
                intent.getOwnerId(),
                payment.getAmount(),
                payment.getId().toString()
        );
    }

    @Transactional
    public void ownerReject(Long paymentId, Long ownerId, String reason) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow();

        PaymentIntent intent = intentRepository
                .findById(payment.getPaymentIntentId())
                .orElseThrow();

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);

        intentService.markFailed(intent, reason);
    }
}
