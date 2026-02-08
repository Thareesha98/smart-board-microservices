package com.sbms.sbms_payment_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_payment_service.entity.PaymentVerification;

public interface PaymentVerificationRepository
extends JpaRepository<PaymentVerification, Long> {

Optional<PaymentVerification> findByPaymentId(Long paymentId);
}
