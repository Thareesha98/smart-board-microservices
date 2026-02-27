package com.sbms.sbms_payment_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import com.sbms.sbms_notification_service.model.enums.PaymentIntentStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentMethod;
import com.sbms.sbms_notification_service.model.enums.PaymentStatus;
import com.sbms.sbms_payment_service.client.FileClient;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.PaymentTransaction;
import com.sbms.sbms_payment_service.events.PaymentSucceededEvent;
import com.sbms.sbms_payment_service.publisher.PaymentEventPublisher;
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
    private final PaymentEventPublisher eventPublisher; // NEW
    
    private final FileClient fileClient;

    @Transactional
    public void verify(Long intentId, Long ownerId, boolean approve) {

        // 1. Load intent FIRST (not transaction)
        PaymentIntent intent = intentRepo.findById(intentId)
                .orElseThrow(() -> new RuntimeException("Payment intent not found"));

        // 2. Try to find existing transaction (card payments will have it)
        PaymentTransaction tx = txRepo.findByIntentId(intent.getId()).orElse(null);

        // 3. If manual payment (CASH / BANK) and no transaction exists → CREATE ONE
        if (tx == null) {
            tx = new PaymentTransaction();
            tx.setIntent(intent);
            tx.setAmount(intent.getAmount());
            tx.setMethod(intent.getMethod()); // CASH or BANK_SLIP
            tx.setStatus(PaymentStatus.AWAITING_VERIFICATION);
            tx.setTransactionRef("MANUAL-" + intent.getId() + "-" + System.currentTimeMillis());
            tx.setVerifiedAt(LocalDateTime.now());
            txRepo.save(tx);
        }

        // 4. Safety checks (NOW works for all methods)
        if (tx.getMethod() == PaymentMethod.CARD) {
            throw new RuntimeException("Card payments cannot be manually verified");
        }

        if (tx.getStatus() != PaymentStatus.AWAITING_VERIFICATION) {
            throw new RuntimeException("Transaction not awaiting verification");
        }

        // 5. Continue your EXISTING logic (no other changes needed)
        if (!approve) {
            tx.setStatus(PaymentStatus.FAILED);
            tx.setFailureReason("Rejected by owner");
            txRepo.save(tx);

            intent.setStatus(PaymentIntentStatus.FAILED);
            intentRepo.save(intent);
            return;
        }

        // SUCCESS FLOW (your existing code below stays SAME)
        tx.setStatus(PaymentStatus.SUCCESS);
        tx.setPaidAt(LocalDateTime.now());
        tx.setVerifiedAt(LocalDateTime.now());
        tx.setVerifiedByOwnerId(ownerId);

        var fees = feeCalculator.calculate(tx.getAmount());
        tx.setPlatformFee(fees.platformFee());
        tx.setGatewayFee(BigDecimal.ZERO);
        tx.setNetAmount(fees.netAmount());

        ownerWalletService.credit(
                intent.getOwnerId(),
                tx.getNetAmount(),
                tx.getTransactionRef()
        );

        byte[] pdf = pdfService.generate(tx);
        String receiptUrl = fileClient.uploadBytes(
                pdf,
                "receipt-" + tx.getTransactionRef() + ".pdf",
                "payment-receipts"
        );
        tx.setReceiptPath(receiptUrl);
        txRepo.save(tx);

        intent.setStatus(PaymentIntentStatus.SUCCESS);
        intent.setCompletedAt(LocalDateTime.now());
        intentRepo.save(intent);

        // publish event (unchanged)
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
