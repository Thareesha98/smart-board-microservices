package com.sbms.sbms_payment_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sbms.sbms_notification_service.model.enums.PaymentIntentStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentMethod;
import com.sbms.sbms_notification_service.model.enums.PaymentStatus;
import com.sbms.sbms_payment_service.client.FileClient;
import com.sbms.sbms_payment_service.dto.GatewayChargeResult;
import com.sbms.sbms_payment_service.dto.PaymentHistoryDTO;
import com.sbms.sbms_payment_service.dto.PaymentResult;
import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.entity.PaymentTransaction;
import com.sbms.sbms_payment_service.events.PaymentSucceededEvent;
import com.sbms.sbms_payment_service.publisher.PaymentEventPublisher;
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
    private final PaymentEventPublisher eventPublisher;
    private final PaymentGateway paymentGateway;
    private final FileClient fileClient;

    @Transactional
    public PaymentResult pay(Long intentId, PaymentMethod method) {

        PaymentIntent intent = intentRepo.findById(intentId)
                .orElseThrow(() -> new RuntimeException("Payment intent not found"));

        if (intent.getStatus() == PaymentIntentStatus.SUCCESS) {
            log.warn("Duplicate payment attempt for intent {}", intentId);
            throw new RuntimeException("Payment already completed");
        }

        // ===================== CARD PAYMENT (SYNC SUCCESS) =====================
        if (method == PaymentMethod.CARD) {

            log.info("Processing CARD payment for intent {}", intentId);

            intent.setStatus(PaymentIntentStatus.SUCCESS);
            intent.setCompletedAt(LocalDateTime.now());
            intent.setMethod(method);
            intentRepo.save(intent);

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

            ownerWalletService.credit(
                    intent.getOwnerId(),
                    netAmount,
                    tx.getTransactionRef()
            );

            String receiptUrl = generateAndUploadReceipt(tx);
            tx.setReceiptPath(receiptUrl);
            txRepo.save(tx);

            publishPaymentSucceededEvent(intent, tx);

            return new PaymentResult(
                    tx.getId(),
                    tx.getTransactionRef(),
                    intent.getAmount().doubleValue(),
                    "SUCCESS",
                    receiptUrl // FIXED: return receipt to frontend
            );
        }

        // ===================== EXTERNAL GATEWAY FLOW =====================
        log.info("Processing gateway payment for intent {}", intentId);

        intent.setStatus(PaymentIntentStatus.PROCESSING);
        intentRepo.save(intent);

		
		GatewayChargeResult gatewayResult;
		
		try {
		    gatewayResult = paymentGateway.charge(intent, method);
		} catch (Exception ex) {
		    //  CRITICAL: Never leave payment in PROCESSING state
		    log.error("Gateway exception for intent {}. Marking as FAILED", intentId, ex);
		
		    intent.setStatus(PaymentIntentStatus.FAILED);
		    intent.setCompletedAt(LocalDateTime.now());
		    intentRepo.save(intent);
		
		    throw new RuntimeException("Payment gateway timeout or failure. Please try again.");
		}
		
		// Handle fallback or failure response
		if (gatewayResult == null || !gatewayResult.isSuccess()) {
		
		    log.warn("Gateway returned failure for intent {}", intentId);
		
		    intent.setStatus(PaymentIntentStatus.FAILED);
		    intent.setCompletedAt(LocalDateTime.now());
		    intentRepo.save(intent);
		
		    throw new RuntimeException("Payment gateway failed");
		}

        PaymentTransaction tx = new PaymentTransaction();
        tx.setIntent(intent);
        tx.setTransactionRef(gatewayResult.getGatewayRef());
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

    // ===================== HELPER: RECEIPT GENERATION =====================
    private String generateAndUploadReceipt(PaymentTransaction tx) {
        try {
            byte[] pdfBytes = receiptPdfService.generate(tx);

            if (pdfBytes == null) {
                log.warn("PDF generation returned null for tx {}", tx.getId());
                return null;
            }

            String fileName = "receipt-" + tx.getTransactionRef() + ".pdf";

            return fileClient.uploadBytes(
                    pdfBytes,
                    fileName,
                    "payment-receipts"
            );

        } catch (Exception ex) {
            log.error("Receipt generation/upload failed for tx {}", tx.getId(), ex);
            // NEVER FAIL PAYMENT FOR RECEIPT ERROR (FINTECH RULE)
            return null;
        }
    }

    // ===================== HELPER: EVENT PUBLISH =====================
    private void publishPaymentSucceededEvent(PaymentIntent intent, PaymentTransaction tx) {
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