package com.sbms.sbms_backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_backend.model.UtilityBill;

import java.util.List;
import java.util.Optional;

public interface UtilityBillRepository extends JpaRepository<UtilityBill, Long> {

    Optional<UtilityBill> findByBoardingIdAndMonth(Long boardingId, String month);

    List<UtilityBill> findByBoardingId(Long boardingId);

 
    List<UtilityBill> findByBoardingIdIn(List<Long> boardingIds);
}
