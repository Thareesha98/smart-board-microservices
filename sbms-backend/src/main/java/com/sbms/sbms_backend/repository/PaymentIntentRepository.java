package com.sbms.sbms_backend.repository;

import com.sbms.sbms_backend.model.PaymentIntent;
import com.sbms.sbms_backend.model.enums.PaymentIntentStatus;
import com.sbms.sbms_backend.model.enums.PaymentType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {

    List<PaymentIntent> findByStudentId(Long studentId);

    List<PaymentIntent> findByBoardingId(Long boardingId);
    
    List<PaymentIntent> findByOwnerId(Long boardingId);
    
    Optional<PaymentIntent> findByMonthlyBillId(Long monthlyBillId);

  
    
    Optional<PaymentIntent> findByStudentIdAndBoardingIdAndType(
            Long studentId,
            Long boardingId,
            PaymentType type
    );
    List<PaymentIntent> findByStatus(PaymentIntentStatus status);
    
    Optional<PaymentIntent> findTopByStudentIdAndBoardingIdAndTypeOrderByCreatedAtDesc(
            Long studentId,
            Long boardingId,
            PaymentType type
    );

}
