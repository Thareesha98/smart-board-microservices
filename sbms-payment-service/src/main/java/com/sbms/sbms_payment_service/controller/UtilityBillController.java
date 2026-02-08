package com.sbms.sbms_payment_service.controller;


import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_payment_service.entity.UtilityBill;
import com.sbms.sbms_payment_service.repository.UtilityBillRepository;
import com.sbms.sbms_payment_service.service.MonthlyBillService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/utilities")
@RequiredArgsConstructor
public class UtilityBillController {

    private final UtilityBillRepository utilityRepo;
    private final MonthlyBillService monthlyBillService;

    @PostMapping("/{boardingId}/{month}")
    public ResponseEntity<?> addUtility(
            @PathVariable Long boardingId,
            @PathVariable String month,
            @RequestHeader("X-User-Id") Long ownerId,
            @RequestParam BigDecimal electricity,
            @RequestParam BigDecimal water
    ) {
        UtilityBill utility = utilityRepo
                .findByBoardingIdAndMonth(boardingId, month)
                .orElseGet(UtilityBill::new);

        utility.setBoardingId(boardingId);
        utility.setOwnerId(ownerId);
        utility.setMonth(month);
        utility.setElectricityAmount(electricity);
        utility.setWaterAmount(water);

        utilityRepo.save(utility);

        // 🔥 TRIGGER BILL GENERATION
        monthlyBillService.generateBillsForUtility(utility);

        return ResponseEntity.ok("Utility bill saved and monthly bills generated");
    }
}
