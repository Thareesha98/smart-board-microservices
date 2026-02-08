package com.sbms.sbms_payment_service.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_payment_service.entity.UtilityBill;


public interface UtilityBillRepository
        extends JpaRepository<UtilityBill, Long> {
	
	Optional<UtilityBill> findByBoardingIdAndMonth(
            Long boardingId,
            String month
    );

    List<UtilityBill> findByMonth(String month);
}
