package com.sbms.sbms_payment_service.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_payment_service.entity.PaymentIntent;
import com.sbms.sbms_payment_service.repository.PaymentIntentRepository;
import com.sbms.sbms_payment_service.service.OwnerPaymentVerificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/owner/payments")
@RequiredArgsConstructor
@Slf4j
public class OwnerPaymentApprovalController {

    private final PaymentIntentRepository intentRepo;
    private final OwnerPaymentVerificationService verificationService;

    @GetMapping("/pendingIntents/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
    public List<PaymentIntent> getAllPendingIntents(@PathVariable Long ownerId) {

        List<PaymentIntent> intents = intentRepo.findByOwnerId(ownerId);

        if (intents.isEmpty()) {
            throw new RuntimeException("Payment intents not found for owner: " + ownerId);
        }

        return intents;
    }
    @PostMapping("/{intentId}/approve")
    @PreAuthorize("hasRole('OWNER')")
    public void approve(
            @PathVariable Long intentId,
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        // Delegates to service (clean architecture)
        verificationService.verify(intentId, ownerId, true);
    }

    @PostMapping("/{intentId}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public void reject(
            @PathVariable Long intentId,
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        verificationService.verify(intentId, ownerId, false);
    }
}
