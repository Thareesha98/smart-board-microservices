package com.sbms.sbms_payment_service.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_payment_service.entity.MonthlyBill;


public interface MonthlyBillRepository
        extends JpaRepository<MonthlyBill, Long> {

    boolean existsByStudentIdAndBoardingIdAndMonth(
            Long studentId,
            Long boardingId,
            String month
    );

    List<MonthlyBill> findByStudentId(Long studentId);

    List<MonthlyBill> findByOwnerId(Long ownerId);
}
