package com.sbms.sbms_backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.sbms.sbms_backend.model.UtilityBill;
import java.util.List;
import java.util.Optional;

public interface UtilityBillRepository extends JpaRepository<UtilityBill, Long> {

    // 1. Existing method (Fixed naming to match Long boardingId field)
    Optional<UtilityBill> findByBoardingIdAndMonth(Long boardingId, String month);

    // 2. FIXED: Replaced findByBoarding_Owner_Id with an 'IN' clause
    // This allows the service to fetch bills for all boardings owned by one person
    List<UtilityBill> findByBoardingIdIn(List<Long> boardingIds);

    // 3. Simple ID lookup
    List<UtilityBill> findByBoardingId(Long boardingId);
    
    // 4. Time-based lookup
    List<UtilityBill> findByMonth(String month);
    
    // 5. Utility for Cron/Batch jobs
    @Query("SELECT DISTINCT u.boardingId FROM UtilityBill u")
    List<Long> findAllDistinctBoardingIds();

    // 6. Optional: Find bills for a specific property over a range of months
    List<UtilityBill> findByBoardingIdOrderByMonthDesc(Long boardingId);
}