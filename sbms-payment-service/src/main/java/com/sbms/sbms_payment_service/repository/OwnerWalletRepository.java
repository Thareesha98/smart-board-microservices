package com.sbms.sbms_payment_service.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_payment_service.entity.OwnerWallet;


public interface OwnerWalletRepository
        extends JpaRepository<OwnerWallet, Long> {

    Optional<OwnerWallet> findByOwnerId(Long ownerId);
}
