package com.sbms.sbms_payment_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_notification_service.model.enums.PaymentIntentStatus;
import com.sbms.sbms_notification_service.model.enums.PaymentType;
import com.sbms.sbms_payment_service.entity.PaymentIntent;

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
