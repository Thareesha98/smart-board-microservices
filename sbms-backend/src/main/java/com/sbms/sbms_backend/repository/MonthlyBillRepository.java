package com.sbms.sbms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_backend.model.MonthlyBill;
import com.sbms.sbms_backend.model.enums.MonthlyBillStatus;

import java.util.List;
import java.util.Optional;

public interface MonthlyBillRepository extends JpaRepository<MonthlyBill, Long> {

    Optional<MonthlyBill> findByStudentIdAndBoardingIdAndMonth(
            Long studentId,
            Long boardingId,
            String month
    );

    List<MonthlyBill> findByStudentId(Long studentId);

    List<MonthlyBill> findByBoardingIdIn(List<Long> boardingIds);
    
    
    Optional<MonthlyBill> findByStudentIdAndBoardingIdAndStatus(
            Long studentId,
            Long boardingId,
            MonthlyBillStatus status
    );
    
}
