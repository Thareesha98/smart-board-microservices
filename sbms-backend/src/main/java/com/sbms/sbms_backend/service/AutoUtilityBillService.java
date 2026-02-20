package com.sbms.sbms_backend.service;



import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbms.sbms_backend.client.BoardingClient;
import com.sbms.sbms_backend.record.BoardingSnapshot;
import com.sbms.sbms_backend.model.UtilityBill;
import com.sbms.sbms_backend.repository.UtilityBillRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AutoUtilityBillService {

    private final BoardingClient boardingClient;
    private final UtilityBillRepository utilityRepo;

    @Transactional
    public void generateForMonth(String month) {
        // 1. Get all unique Boarding IDs that already have bills or exist in your system
        List<Long> knownBoardingIds = utilityRepo.findAllDistinctBoardingIds();

        if (knownBoardingIds.isEmpty()) return;

        // 2. Use existing Client method to get data for these IDs
        List<BoardingSnapshot> boardings = boardingClient.getBoardingSnapshots(knownBoardingIds);

        for (BoardingSnapshot boarding : boardings) {

            boolean exists = utilityRepo
                    .findByBoardingIdAndMonth(boarding.id(), month)
                    .isPresent();

            if (exists) continue;

            UtilityBill bill = new UtilityBill();
            bill.setBoardingId(boarding.id()); // Use ID, not Entity
            bill.setMonth(month);

            // 🔹 Default / estimated utilities using Record accessors
            bill.setElectricityAmount(defaultElectricity(boarding));
            bill.setWaterAmount(defaultWater(boarding));

            utilityRepo.save(bill);
        }
    }

    private BigDecimal defaultElectricity(BoardingSnapshot boarding) {
        return boarding.pricePerMonth().multiply(new BigDecimal("0.15"));
    }

    private BigDecimal defaultWater(BoardingSnapshot boarding) {
        return boarding.pricePerMonth().multiply(new BigDecimal("0.05"));
    }
}