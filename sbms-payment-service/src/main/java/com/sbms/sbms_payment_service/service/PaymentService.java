package com.sbms.sbms_payment_service.service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import com.sbms.sbms_notification_service.model.enums.PaymentIntentStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentMethod;
import com.sbms.sbms_notification_service.model.enums.PaymentStatus;
import com.sbms.sbms_payment_service.dto.GatewayChargeResult;
import com.sbms.sbms_payment_service.dto.PaymentHistoryDTO;
import com.sbms.sbms_payment_service.dto.PaymentResult;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.PaymentTransaction;
import com.sbms.sbms_payment_service.events.PaymentSucceededEvent;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;
import com.sbms.sbms_payment_service.repository.PaymentTransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentIntentRepository intentRepo;
    private final PaymentTransactionRepository txRepo;
    
    private final PaymentReceiptPdfService receiptPdfService;
    private final OwnerWalletService ownerWalletService;
    
    private final S3Service s3Service;
    
    private final PaymentEventPublisher eventPublisher;
    
    private final PaymentGateway paymentGateway;


    
    
    
    
    
    @Transactional
    public PaymentResult pay(Long intentId, PaymentMethod method) {

        PaymentIntent intent = intentRepo.findById(intentId)
                .orElseThrow(() -> new RuntimeException("Payment intent not found"));

        // Idempotency check (CRITICAL)
        if (intent.getStatus() == PaymentIntentStatus.SUCCESS) {
            log.warn("Duplicate payment attempt for intent {}", intentId);
            throw new RuntimeException("Payment already completed");
        }

        if (method == PaymentMethod.CARD) {

            // 1️⃣ Update Intent (LOCAL DB ONLY)
            intent.setStatus(PaymentIntentStatus.SUCCESS);
            intent.setCompletedAt(LocalDateTime.now());
            intentRepo.save(intent);

            // 2️⃣ Create Transaction
            PaymentTransaction tx = new PaymentTransaction();
            tx.setIntent(intent);
            tx.setMethod(method);
            tx.setGateway("PAYHERE");
            tx.setStatus(PaymentStatus.SUCCESS);
            tx.setAmount(intent.getAmount());
            tx.setPaidAt(LocalDateTime.now());
            tx.setVerifiedAt(LocalDateTime.now());
            tx.setTransactionRef("PH-" + intent.getId());

            BigDecimal platformFee = intent.getAmount()
                    .multiply(new BigDecimal("0.02"));

            BigDecimal gatewayFee = intent.getAmount()
                    .multiply(new BigDecimal("0.01"));

            tx.setPlatformFee(platformFee);
            tx.setGatewayFee(gatewayFee);

            BigDecimal netAmount =
                    intent.getAmount().subtract(platformFee).subtract(gatewayFee);

            tx.setNetAmount(netAmount);
            txRepo.save(tx);

            // 3️⃣ Credit Wallet (Still in Payment Domain = OK)
            ownerWalletService.credit(
                    intent.getOwnerId(),
                    netAmount,
                    tx.getTransactionRef()
            );

            // 4️⃣ PUBLISH DOMAIN EVENT (Instead of updating MonthlyBill)
            PaymentSucceededEvent event = new PaymentSucceededEvent();
            event.setIntentId(intent.getId());
            event.setTransactionId(tx.getId());
            event.setStudentId(intent.getStudentId());
            event.setOwnerId(intent.getOwnerId());
            event.setMonthlyBillId(intent.getMonthlyBillId());
            event.setAmount(tx.getAmount());
            event.setMethod(method.name());
            event.setTransactionRef(tx.getTransactionRef());

            eventPublisher.publishPaymentSucceeded(event);

            // 5️⃣ Return response (FAST API, async processing)
            return new PaymentResult(
                    tx.getId(),
                    tx.getTransactionRef(),
                    intent.getAmount().doubleValue(),
                    "SUCCESS",
                    null // receipt now async
            );
        }

        // Gateway async flow (unchanged)
        intent.setStatus(PaymentIntentStatus.PROCESSING);
        intentRepo.save(intent);

        GatewayChargeResult gateway =
                paymentGateway.charge(intent, method);

        PaymentTransaction tx = new PaymentTransaction();
        tx.setIntent(intent);
        tx.setTransactionRef(gateway.getGatewayRef());
        tx.setAmount(intent.getAmount());
        tx.setMethod(method);
        tx.setGateway("PAYHERE");
        tx.setStatus(PaymentStatus.PENDING);

        txRepo.save(tx);

        return new PaymentResult(
                tx.getId(),
                tx.getTransactionRef(),
                intent.getAmount().doubleValue(),
                "PENDING",
                null
        );
    }

    
    
    
    
    
    


    public List<PaymentHistoryDTO> history(Long userId) {

        return txRepo.findByIntentStudentId(userId)
                .stream()
                .map(tx -> {
                    PaymentHistoryDTO dto = new PaymentHistoryDTO();
                    dto.setId(tx.getId());
                    dto.setTransactionRef(tx.getTransactionRef());
                    dto.setAmount(tx.getAmount());
                    dto.setMethod(tx.getMethod());
                    dto.setStatus(tx.getStatus());
                    dto.setPaidAt(tx.getPaidAt());
                    dto.setReceiptUrl(tx.getReceiptPath());
                    return dto;
                })
                .toList();
    }
}

