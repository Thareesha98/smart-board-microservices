package com.sbms.sbms_payment_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_payment_service.entity.PaymentIntent;

import java.util.Optional;

public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {

    Optional<PaymentIntent> findByReferenceTypeAndReferenceId(
            String referenceType,
            String referenceId
    );
}
