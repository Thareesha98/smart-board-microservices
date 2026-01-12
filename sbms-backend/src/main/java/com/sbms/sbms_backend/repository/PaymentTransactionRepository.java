package com.sbms.sbms_backend.repository;


import com.sbms.sbms_backend.model.PaymentTransaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, Long> {
	List<PaymentTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}
