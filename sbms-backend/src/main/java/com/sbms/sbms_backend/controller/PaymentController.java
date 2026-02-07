package com.sbms.sbms_backend.controller;

import com.sbms.sbms_backend.client.UserClient;
import com.sbms.sbms_backend.dto.payment.*;
import com.sbms.sbms_backend.dto.user.UserMinimalDTO;
import com.sbms.sbms_backend.model.PaymentIntent;
import com.sbms.sbms_backend.model.PaymentTransaction;
import com.sbms.sbms_backend.model.enums.ManualApprovalStatus;
import com.sbms.sbms_backend.model.enums.PaymentIntentStatus;
import com.sbms.sbms_backend.model.enums.PaymentMethod;
import com.sbms.sbms_backend.model.enums.PaymentStatus;
import com.sbms.sbms_backend.model.enums.PaymentType;
import com.sbms.sbms_backend.repository.PaymentIntentRepository;
import com.sbms.sbms_backend.repository.PaymentTransactionRepository;

import com.sbms.sbms_backend.service.BankSlipPaymentService;
import com.sbms.sbms_backend.service.CashPaymentService;
import com.sbms.sbms_backend.service.PaymentIntentService;
import com.sbms.sbms_backend.service.PaymentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private final   UserClient userClient;
    


    // 1️ CREATE PAYMENT INTENT
    @PostMapping("/intent")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentIntent> createIntent(
            @RequestBody CreatePaymentIntentDTO dto,
            Authentication authentication
    ) {
        // 1. Get user email from JWT
        String email = authentication.getName();

        // 2. Resolve student ID via UserClient (Microservices Pattern)
        UserMinimalDTO student = userClient.findByEmail(email);
        
        if (student == null) {
            throw new RuntimeException("Student not found in User Service");
        }

        // 3. FORCE studentId from resolved data (prevents ID spoofing from frontend)
        dto.setStudentId(student.getId());

        // 4. Delegate to service layer
        return ResponseEntity.ok(paymentIntentService.create(dto));
    }

    
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


    // 2️ CARD PAYMENT (PAYHERE)
    @PostMapping("/pay/{intentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentResult> pay(
            @PathVariable Long intentId,
            @RequestParam PaymentMethod method
    ) {
        return ResponseEntity.ok(paymentService.pay(intentId, method));
    }

   
    
    
    @PostMapping("/cash/{intentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> cashPayment(
            @PathVariable Long intentId,
            Authentication authentication
    ) {
        String email = authentication.getName(); // JWT subject

        PaymentIntent intent = paymentIntentRepo.findById(intentId)
                .orElseThrow(() -> new RuntimeException("Payment intent not found"));

        // ✅ JWT-based ownership check
       

        cashPaymentService.createCashPayment(intentId);
        return ResponseEntity.ok("CASH_PAYMENT_SUBMITTED");
    }



    // 4️ BANK SLIP (URL-BASED, NO MULTIPART)
    @PostMapping("/bank-slip/{intentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> submitBankSlipUrl(
            @PathVariable Long intentId,
            @RequestParam String slipUrl,
            @RequestHeader("X-User-Id") Long studentId
    ) {
        PaymentIntent intent = paymentIntentRepo.findById(intentId)
                .orElseThrow(() -> new RuntimeException("Payment intent not found"));

        // 🔒 OWNERSHIP
        if (!intent.getStudentId().equals(studentId)) {
            throw new RuntimeException("Unauthorized access");
        }

        bankSlipPaymentService.attachSlipUrl(intentId, slipUrl);

        return ResponseEntity.ok("SLIP_SUBMITTED");
    }

    
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

    // 6️ KEY MONEY STATUS (FOR REGISTRATION PAGE)
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

        //  STUDENT VIEW: paid OR pending verification
        boolean visibleAsPaid =
                status == PaymentIntentStatus.SUCCESS
             || status == PaymentIntentStatus.AWAITING_MANUAL_APPROVAL;

        return ResponseEntity.ok(visibleAsPaid);
    }
    
    
    
    
    
    
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

        paymentIntentRepo.save(intent); //  FIXED
    }

}
