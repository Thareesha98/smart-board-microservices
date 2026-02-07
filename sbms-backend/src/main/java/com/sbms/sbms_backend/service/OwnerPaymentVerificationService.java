package com.sbms.sbms_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sbms.sbms_backend.client.EmailClient;
import com.sbms.sbms_backend.client.UserClient;
import com.sbms.sbms_backend.dto.user.UserMinimalDTO;
import com.sbms.sbms_backend.model.MonthlyBill;
import com.sbms.sbms_backend.model.PaymentIntent;
import com.sbms.sbms_backend.model.PaymentTransaction;
import com.sbms.sbms_backend.model.enums.MonthlyBillStatus;
import com.sbms.sbms_backend.model.enums.PaymentIntentStatus;
import com.sbms.sbms_backend.model.enums.PaymentMethod;
import com.sbms.sbms_backend.model.enums.PaymentStatus;
import com.sbms.sbms_backend.repository.MonthlyBillRepository;
import com.sbms.sbms_backend.repository.PaymentIntentRepository;
import com.sbms.sbms_backend.repository.PaymentTransactionRepository;

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
    private final MonthlyBillRepository monthlyBillRepo;

    // FIX: Use Clients instead of local Repos/Services
    private final UserClient userClient;
    private final EmailClient emailClient;

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
        String receiptUrl = s3Service.uploadBytes(
                pdf,
                "payment-receipts/" + tx.getTransactionRef() + ".pdf",
                "application/pdf"
        );
        tx.setReceiptPath(receiptUrl);
        txRepo.save(tx);

        // INTENT COMPLETE
        intent.setStatus(PaymentIntentStatus.SUCCESS);
        intent.setCompletedAt(LocalDateTime.now());
        intentRepo.save(intent);
        
        // UPDATE BILL STATUS IF APPLICABLE
        if (intent.getMonthlyBillId() != null) {
            MonthlyBill bill = monthlyBillRepo.findById(intent.getMonthlyBillId())
                    .orElseThrow();
            bill.setStatus(MonthlyBillStatus.PAID);
            monthlyBillRepo.save(bill);
        }

        /* ---------- EMAIL NOTIFICATION (CROSS-SERVICE) ---------- */

        // 1. Fetch Student details from User Service
        UserMinimalDTO student = userClient.getUserMinimal(intent.getStudentId());
        
        if (student != null) {
            // 2. Use the specialized EmailClient to trigger the User Service's email sender
            emailClient.sendPaymentReceipt(
                    student.getEmail(),
                    student.getFullName(),
                    tx.getTransactionRef(), // Receipt Number
                    pdf
            );
        }
    }
}