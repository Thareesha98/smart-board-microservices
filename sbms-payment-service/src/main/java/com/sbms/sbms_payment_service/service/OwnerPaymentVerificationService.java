package com.sbms.sbms_payment_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import com.sbms.sbms_notification_service.model.enums.PaymentIntentStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentMethod;
import com.sbms.sbms_notification_service.model.enums.PaymentStatus;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.PaymentTransaction;
import com.sbms.sbms_payment_service.events.PaymentSucceededEvent;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;
import com.sbms.sbms_payment_service.repository.PaymentTransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;











@Service
@RequiredArgsConstructor
public class OwnerPaymentVerificationService {

    private final PaymentTransactionRepository txRepo;
    private final PaymentIntentRepository intentRepo;
    private final PaymentFeeCalculator feeCalculator;
    private final OwnerWalletService ownerWalletService;
    private final PaymentReceiptPdfService pdfService;
    private final S3Service s3Service;
    private final PaymentEventPublisher eventPublisher; // NEW

    @Transactional
    public void verify(Long txId, Long ownerId, boolean approve) {

        PaymentTransaction tx = txRepo.findById(txId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (tx.getMethod() == PaymentMethod.CARD) {
            throw new RuntimeException("Card payments cannot be manually verified");
        }

        if (tx.getStatus() != PaymentStatus.AWAITING_VERIFICATION) {
            throw new RuntimeException("Transaction not awaiting verification");
        }

        PaymentIntent intent = tx.getIntent();

        if (!approve) {
            tx.setStatus(PaymentStatus.FAILED);
            tx.setFailureReason("Rejected by owner");
            txRepo.save(tx);

            intent.setStatus(PaymentIntentStatus.FAILED);
            intentRepo.save(intent);
            return;
        }

        // SUCCESS FLOW
        tx.setStatus(PaymentStatus.SUCCESS);
        tx.setPaidAt(LocalDateTime.now());
        tx.setVerifiedAt(LocalDateTime.now());
        tx.setVerifiedByOwnerId(ownerId);

        var fees = feeCalculator.calculate(tx.getAmount());
        tx.setPlatformFee(fees.platformFee());
        tx.setGatewayFee(BigDecimal.ZERO);
        tx.setNetAmount(fees.netAmount());

        // Wallet credit (PAYMENT DOMAIN = OK)
        ownerWalletService.credit(
                intent.getOwnerId(),
                tx.getNetAmount(),
                tx.getTransactionRef()
        );

        // Receipt generation (can later be async service)
        byte[] pdf = pdfService.generate(tx);
        String receiptUrl = s3Service.uploadBytes(
                pdf,
                "payment-receipts/" + tx.getTransactionRef() + ".pdf",
                "application/pdf"
        );
        tx.setReceiptPath(receiptUrl);
        txRepo.save(tx);

        // COMPLETE INTENT
        intent.setStatus(PaymentIntentStatus.SUCCESS);
        intent.setCompletedAt(LocalDateTime.now());
        intentRepo.save(intent);

        // 🚀 PUBLISH EVENT (INSTEAD OF UPDATING BILL TABLE)
        PaymentSucceededEvent event = new PaymentSucceededEvent();
        event.setIntentId(intent.getId());
        event.setTransactionId(tx.getId());
        event.setStudentId(intent.getStudentId());
        event.setOwnerId(intent.getOwnerId());
        event.setMonthlyBillId(intent.getMonthlyBillId());
        event.setAmount(tx.getAmount());
        event.setMethod(tx.getMethod().name());
        event.setTransactionRef(tx.getTransactionRef());

        eventPublisher.publishPaymentSucceeded(event);
    }
}
