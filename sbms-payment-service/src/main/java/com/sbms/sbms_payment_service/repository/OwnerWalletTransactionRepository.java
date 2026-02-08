package com.sbms.sbms_payment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_payment_service.entity.OwnerWalletTransaction;

public interface OwnerWalletTransactionRepository
        extends JpaRepository<OwnerWalletTransaction, Long> {
}
