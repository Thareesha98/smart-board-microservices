package com.sbms.sbms_payment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_payment_service.entity.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByGatewayAndGatewayReference(
            String gateway,
            String gatewayReference
    );
}
