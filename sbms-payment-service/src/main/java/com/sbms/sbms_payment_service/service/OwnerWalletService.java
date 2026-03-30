package com.sbms.sbms_payment_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sbms.sbms_payment_service.entity.OwnerWallet;
import com.sbms.sbms_payment_service.entity.OwnerWalletTransaction;
import com.sbms.sbms_payment_service.repository.OwnerWalletRepository;
import com.sbms.sbms_payment_service.repository.OwnerWalletTransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerWalletService {

    private final OwnerWalletRepository walletRepo;
    private final OwnerWalletTransactionRepository txRepo;

    @Transactional
    public void credit(Long ownerId, BigDecimal amount, String reference) {

        OwnerWallet wallet = walletRepo.findByOwnerId(ownerId)
                .orElseGet(() -> {
                    OwnerWallet w = new OwnerWallet();
                    w.setOwnerId(ownerId);
                    w.setBalance(BigDecimal.ZERO);
                    w.setLastUpdated(LocalDateTime.now());
                    return walletRepo.save(w);
                });

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setLastUpdated(LocalDateTime.now());
        walletRepo.save(wallet);
        
        OwnerWalletTransaction tx = new OwnerWalletTransaction();
        tx.setOwnerId(ownerId);
        tx.setAmount(amount);
        tx.setReference(reference);
        tx.setType("CREDIT");
        txRepo.save(tx);
    }
    
    
    
    
    @Transactional
    public void debit(Long ownerId, BigDecimal amount, String reference) {

        OwnerWallet wallet = walletRepo.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for owner " + ownerId));

        // =========================
        // 🔥 IDEMPOTENCY CHECK
        // =========================
        boolean alreadyDebited = txRepo
                .existsByReferenceAndType(reference, "DEBIT");

        if (alreadyDebited) {
            log.warn("Duplicate rollback prevented for ref={}", reference);
            return;
        }


        if (wallet.getBalance().compareTo(amount) < 0) {
            log.error("Insufficient balance for rollback. owner={} balance={} required={}",
                    ownerId, wallet.getBalance(), amount);

            throw new RuntimeException("Wallet balance insufficient for rollback");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setLastUpdated(LocalDateTime.now());
        walletRepo.save(wallet);

        OwnerWalletTransaction tx = new OwnerWalletTransaction();
        tx.setOwnerId(ownerId);
        tx.setAmount(amount);
        tx.setReference(reference);
        tx.setType("DEBIT");
        tx.setCreatedAt(LocalDateTime.now());
        txRepo.save(tx);

        log.error("Wallet debited (ROLLBACK): owner={} amount={} ref={}", ownerId, amount, reference);
    }
}
