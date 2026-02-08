package com.sbms.sbms_payment_service.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sbms.sbms_payment_service.entity.OwnerWalletTransaction;
import com.sbms.sbms_payment_service.repository.OwnerWalletTransactionRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OwnerWalletCreditService {

    private final OwnerWalletTransactionRepository repository;

    @Transactional
    public void credit(
            Long ownerId,
            BigDecimal amount,
            String paymentReference
    ) {
        // DB unique constraint ensures idempotency
        OwnerWalletTransaction tx = new OwnerWalletTransaction();
        tx.setOwnerId(ownerId);
        tx.setAmount(amount);
        tx.setReference(paymentReference);

        repository.save(tx);
    }
}
