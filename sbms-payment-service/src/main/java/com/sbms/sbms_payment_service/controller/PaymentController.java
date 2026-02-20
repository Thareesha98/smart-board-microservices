package com.sbms.sbms_payment_service.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_notification_service.model.enums.ManualApprovalStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentIntentStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentMethod;
import com.sbms.sbms_notification_service.model.enums.PaymentStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentType;
import com.sbms.sbms_payment_service.client.UserClient;
import com.sbms.sbms_payment_service.dto.CreatePaymentIntentDTO;
import com.sbms.sbms_payment_service.dto.PaymentHistoryDTO;
import com.sbms.sbms_payment_service.dto.PaymentResult;
import com.sbms.sbms_payment_service.dto.user.UserMinimalDTO;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.PaymentTransaction;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;
import com.sbms.sbms_payment_service.repository.PaymentTransactionRepository;
import com.sbms.sbms_payment_service.service.BankSlipPaymentService;
import com.sbms.sbms_payment_service.service.CashPaymentService;
import com.sbms.sbms_payment_service.service.PaymentIntentService;
import com.sbms.sbms_payment_service.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentIntentService paymentIntentService;
    private final CashPaymentService cashPaymentService;
    private final BankSlipPaymentService bankSlipPaymentService;
    private final PaymentIntentRepository paymentIntentRepo;
    private final PaymentTransactionRepository txRepo;
    private final UserClient userClient;

    // ===============================
    // 1️⃣ CREATE PAYMENT INTENT (UNCHANGED FOR FRONTEND)
    // ===============================
    @PostMapping("/intent")
    public ResponseEntity<PaymentIntent> createIntent(
            @RequestBody CreatePaymentIntentDTO dto,
            Authentication authentication
    ) {
        String email = authentication.getName();

        UserMinimalDTO student = userClient.findByEmail(email);

        if (student == null) {
            throw new RuntimeException("Student not found in User Service");
        }

        // SECURITY: prevent spoofing
        dto.setStudentId(student.getId());

        return ResponseEntity.ok(paymentIntentService.create(dto));
    }

    // ===============================
    // 2️⃣ CARD / ONLINE PAYMENT (UNCHANGED API)
    // ===============================
    @PostMapping("/pay/{intentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentResult> pay(
            @PathVariable Long intentId,
            @RequestParam PaymentMethod method
    ) {
        return ResponseEntity.ok(paymentService.pay(intentId, method));
    }

    // ===============================
    // 3️⃣ CASH PAYMENT (UNCHANGED RESPONSE)
    // ===============================
    @PostMapping("/cash/{intentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> cashPayment(
            @PathVariable Long intentId,
            Authentication authentication
    ) {
        // Frontend behavior preserved
        cashPaymentService.createCashPayment(intentId);
        return ResponseEntity.ok("CASH_PAYMENT_SUBMITTED");
    }

    // ===============================
    // ===============================
    @PostMapping("/bank-slip/{intentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> submitBankSlipUrl(
            @PathVariable Long intentId,
            @RequestParam String slipUrl,
            @RequestHeader("X-User-Id") Long studentId
    ) {
        PaymentIntent intent = paymentIntentRepo.findById(intentId)
                .orElseThrow(() -> new RuntimeException("Payment intent not found"));

        // Ownership validation (IMPORTANT)
        if (!intent.getStudentId().equals(studentId)) {
            throw new RuntimeException("Unauthorized access");
        }

        bankSlipPaymentService.attachSlipUrl(intentId, slipUrl);

        return ResponseEntity.ok("SLIP_SUBMITTED");
    }

    // ===============================
    // 5️⃣ PAYMENT HISTORY (UNCHANGED)
    // ===============================
    @GetMapping("/history")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<PaymentHistoryDTO>> history(Authentication authentication) {

        String email = authentication.getName();

        UserMinimalDTO student = userClient.findByEmail(email);

        if (student == null) {
            throw new RuntimeException("Student record not found in User Service");
        }

        return ResponseEntity.ok(paymentService.history(student.getId()));
    }

    // ===============================
    // 6️⃣ KEY MONEY STATUS (CRITICAL FOR FRONTEND UI)
    // ===============================
    @GetMapping("/key-money-status")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> keyMoneyStatus(
            @RequestParam Long boardingId,
            @RequestHeader("X-User-Id") Long studentId
    ) {
        Optional<PaymentIntent> intentOpt =
                paymentIntentRepo.findTopByStudentIdAndBoardingIdAndTypeOrderByCreatedAtDesc(
                        studentId,
                        boardingId,
                        PaymentType.KEY_MONEY
                );

        if (intentOpt.isEmpty()) {
            return ResponseEntity.ok(false);
        }

        PaymentIntentStatus status = intentOpt.get().getStatus();

        boolean visibleAsPaid =
                status == PaymentIntentStatus.SUCCESS
             || status == PaymentIntentStatus.AWAITING_MANUAL_APPROVAL;

        return ResponseEntity.ok(visibleAsPaid);
    }

    // ===============================
    // 7️⃣ OWNER REJECT (UNCHANGED BEHAVIOR)
    // ===============================
    @PostMapping("/{intentId}/reject")
    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public void reject(@PathVariable Long intentId) {

        PaymentIntent intent = paymentIntentRepo.findById(intentId)
                .orElseThrow(() -> new RuntimeException("Payment intent not found"));

        if (intent.getStatus() != PaymentIntentStatus.AWAITING_MANUAL_APPROVAL) {
            throw new RuntimeException("Payment cannot be rejected");
        }

        intent.setManualApprovalStatus(ManualApprovalStatus.REJECTED);
        intent.setStatus(PaymentIntentStatus.FAILED);

        paymentIntentRepo.save(intent);
    }

    // ===============================
    // 8️⃣ PAYMENT GATEWAY CALLBACK (CRITICAL)
    // ===============================
    @PostMapping("/gateway/callback")
    @Transactional
    public void handleGatewayCallback(
            @RequestParam String transactionRef,
            @RequestParam boolean success
    ) {
        PaymentTransaction tx = txRepo
                .findByTransactionRef(transactionRef)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!success) {
            tx.setStatus(PaymentStatus.FAILED);
            txRepo.save(tx);
            return;
        }

        tx.setStatus(PaymentStatus.SUCCESS);
        tx.setPaidAt(LocalDateTime.now());
        txRepo.save(tx);

        PaymentIntent intent = tx.getIntent();
        intent.setStatus(PaymentIntentStatus.SUCCESS);
        intent.setMethod(tx.getMethod());
        intent.setCompletedAt(LocalDateTime.now());

        paymentIntentRepo.save(intent);

        // NOTE: Event publishing should be inside service layer (best practice)
    }
}
