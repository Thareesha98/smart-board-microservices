package com.sbms.sbms_backend.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_backend.model.OwnerWallet;

public interface OwnerWalletRepository
        extends JpaRepository<OwnerWallet, Long> {

    Optional<OwnerWallet> findByOwnerId(Long ownerId);
}
