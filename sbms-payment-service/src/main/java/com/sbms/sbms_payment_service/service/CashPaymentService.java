package com.sbms.sbms_payment_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbms.sbms_notification_service.model.enums.ManualApprovalStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentIntentStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentMethod;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.events.PaymentPendingApprovalEvent;
import com.sbms.sbms_payment_service.publisher.PaymentEventPublisher;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;

import lombok.RequiredArgsConstructor;




@Service
@RequiredArgsConstructor
public class CashPaymentService {

    private final PaymentIntentRepository intentRepo;
    private final PaymentEventPublisher eventPublisher; // NEW

    @Transactional
    public void createCashPayment(Long intentId) {

        PaymentIntent intent = intentRepo.findById(intentId)
                .orElseThrow(() -> new RuntimeException("Payment intent not found"));

        if (intent.getStatus() == PaymentIntentStatus.SUCCESS) {
            throw new IllegalStateException("Payment already completed");
        }

        if (intent.getStatus() == PaymentIntentStatus.AWAITING_MANUAL_APPROVAL) {
            return; // idempotent
        }

        intent.setStatus(PaymentIntentStatus.AWAITING_MANUAL_APPROVAL);
        intent.setManualApprovalStatus(ManualApprovalStatus.PENDING);
        intent.setCompletedAt(LocalDateTime.now());
        intent.setMethod(PaymentMethod.CASH);

        intentRepo.save(intent);

        PaymentPendingApprovalEvent event = new PaymentPendingApprovalEvent();
        event.setIntentId(intent.getId());
        event.setStudentId(intent.getStudentId());
        event.setOwnerId(intent.getOwnerId());
        event.setMonthlyBillId(intent.getMonthlyBillId());
        event.setMethod(PaymentMethod.CASH.name());

        eventPublisher.publishPendingApproval(event);
    }
}
