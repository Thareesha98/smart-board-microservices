package com.sbms.sbms_payment_service.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sbms.sbms_notification_service.model.enums.ManualApprovalStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentIntentStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentMethod;
import com.sbms.sbms_payment_service.client.FileClient;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.events.PaymentPendingApprovalEvent;
import com.sbms.sbms_payment_service.publisher.PaymentEventPublisher;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankSlipPaymentService {

    private final PaymentIntentRepository intentRepo;
    private final FileClient fileClient;
    private final PaymentEventPublisher eventPublisher;

    // ============================================
    @Transactional
    public void uploadSlip(Long intentId, MultipartFile slip) throws IOException {

        PaymentIntent intent = intentRepo.findById(intentId)
                .orElseThrow(() -> new RuntimeException("Payment intent not found"));

        validateIntentForSlipSubmission(intent);

        // Upload to S3
        String slipUrl = fileClient.uploadBytes(
                slip.getBytes(),
                "bank-slip-" + intent.getId() + ".jpg",
                "bank-slips"
        );

        processSlipSubmission(intent, slipUrl);
    }

    // ============================================
    // ============================================
    @Transactional
    public void attachSlipUrl(Long intentId, String slipUrl) {

        PaymentIntent intent = intentRepo.findById(intentId)
                .orElseThrow(() -> new RuntimeException("Payment intent not found"));

        validateIntentForSlipSubmission(intent);

        processSlipSubmission(intent, slipUrl);
    }

    // ============================================
    // ============================================
    private void validateIntentForSlipSubmission(PaymentIntent intent) {

        if (intent.getStatus() == PaymentIntentStatus.SUCCESS) {
            throw new IllegalStateException("Payment already completed");
        }

        if (intent.getStatus() == PaymentIntentStatus.AWAITING_MANUAL_APPROVAL) {
            log.info("Slip already submitted for intent {}", intent.getId());
            return; // idempotent behavior
        }

        if (intent.getStatus() != PaymentIntentStatus.CREATED
                && intent.getStatus() != PaymentIntentStatus.FAILED) {
            throw new IllegalStateException(
                    "Cannot submit slip in current state: " + intent.getStatus()
            );
        }
    }

    // ============================================
    // ============================================
    private void processSlipSubmission(PaymentIntent intent, String slipUrl) {

        intent.setReferenceId(slipUrl);
        intent.setMethod(PaymentMethod.BANK_SLIP);
        intent.setStatus(PaymentIntentStatus.AWAITING_MANUAL_APPROVAL);
        intent.setManualApprovalStatus(ManualApprovalStatus.PENDING);
        intent.setCompletedAt(LocalDateTime.now());

        intentRepo.save(intent);

        //  Publish event (async billing + notification)
        PaymentPendingApprovalEvent event = new PaymentPendingApprovalEvent();
        event.setIntentId(intent.getId());
        event.setStudentId(intent.getStudentId());
        event.setOwnerId(intent.getOwnerId());
        event.setMonthlyBillId(intent.getMonthlyBillId());
        event.setMethod(PaymentMethod.BANK_SLIP.name());

        eventPublisher.publishPendingApproval(event);

        log.info("Bank slip submitted for intent {}", intent.getId());
    }
}
