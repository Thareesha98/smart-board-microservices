package com.sbms.sbms_backend.service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbms.sbms_backend.client.BoardingClient;
import com.sbms.sbms_backend.dto.billing.CreateUtilityBillDTO;
import com.sbms.sbms_backend.dto.billing.UtilityBillResponseDTO;
import com.sbms.sbms_backend.mapper.UtilityBillMapper;
import com.sbms.sbms_backend.model.UtilityBill;
import com.sbms.sbms_backend.model.enums.RegistrationStatus;
import com.sbms.sbms_backend.record.BoardingSnapshot;
import com.sbms.sbms_backend.repository.RegistrationRepository;
import com.sbms.sbms_backend.repository.UtilityBillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UtilityBillService {

    private final UtilityBillRepository utilityRepo;
    private final RegistrationRepository registrationRepo;
    private final MonthlyBillService monthlyBillService;
    private final BoardingClient boardingClient;

    
        @Transactional
        public void createOrUpdate(CreateUtilityBillDTO dto) {

            // 1. Verify the boarding exists in the Boarding Service
            // This replaces boardingRepo.findById()
            BoardingSnapshot boarding = boardingClient.getBoarding(dto.getBoardingId());
            if (boarding == null) {
                throw new RuntimeException("Boarding house not found");
            }

            // 2. Find existing bill or create new (Match your new repository name)
            UtilityBill bill = utilityRepo
                    .findByBoardingIdAndMonth(dto.getBoardingId(), dto.getMonth())
                    .orElseGet(UtilityBill::new);

            // 3. Set properties (Using ID, not the Boarding object)
            bill.setBoardingId(dto.getBoardingId()); 
            bill.setMonth(dto.getMonth());
            bill.setElectricityAmount(dto.getElectricityAmount());
            bill.setWaterAmount(dto.getWaterAmount());
            bill.setProofUrl(dto.getProofUrl());

            utilityRepo.save(bill);

            //  IMPORTANT: Trigger student bill generation
            // This will fetch snapshots and registrations internally as we fixed earlier
            monthlyBillService.generateBillsForMonth(dto.getMonth());
        }
        public List<UtilityBillResponseDTO> getForOwner(Long ownerId) {
            // 1. Ask Boarding Service: "Which boarding houses does this owner have?"
            List<Long> boardingIds = boardingClient.getBoardingIdsByOwner(ownerId);

            if (boardingIds.isEmpty()) {
                return List.of();
            }

            // 2. Fetch Utility bills from local DB where boardingId is in the list
            return utilityRepo.findByBoardingIdIn(boardingIds)
                    .stream()
                    .map(this::map)
                    .toList();
        }

        private UtilityBillResponseDTO map(UtilityBill bill) {
            // ... (This logic remains the same as our previous fix) ...
            // It fetches Boarding Title from Client and Student Count from local Reg Repo
            BoardingSnapshot boarding = boardingClient.getBoarding(bill.getBoardingId());
            String boardingName = (boarding != null) ? boarding.title() : "Unknown Boarding";

            int studentCount = (int) registrationRepo.countByBoardingIdAndStatus(
                    bill.getBoardingId(), RegistrationStatus.APPROVED);

            BigDecimal total = bill.getElectricityAmount().add(bill.getWaterAmount());
            BigDecimal perStudent = studentCount == 0 ? BigDecimal.ZERO 
                    : total.divide(BigDecimal.valueOf(studentCount), 2, RoundingMode.HALF_UP);

            UtilityBillResponseDTO dto = new UtilityBillResponseDTO();
            dto.setId(bill.getId());
            dto.setBoardingId(bill.getBoardingId());
            dto.setBoardingName(boardingName);
            dto.setMonth(bill.getMonth());
            dto.setElectricityAmount(bill.getElectricityAmount());
            dto.setWaterAmount(bill.getWaterAmount());
            dto.setPerStudentUtility(perStudent);
            return dto;
        }
}

