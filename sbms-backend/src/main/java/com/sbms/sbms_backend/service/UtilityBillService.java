package com.sbms.sbms_backend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbms.sbms_backend.dto.utility.UtilityBillCreateDTO;
import com.sbms.sbms_backend.dto.utility.UtilityBillResponseDTO;
import com.sbms.sbms_backend.mapper.UtilityBillMapper;
import com.sbms.sbms_backend.model.Boarding;
import com.sbms.sbms_backend.model.UtilityBill;
import com.sbms.sbms_backend.repository.BoardingRepository;
import com.sbms.sbms_backend.repository.UtilityBillRepository;

import java.util.List;

@Service
public class UtilityBillService {

    @Autowired
    private UtilityBillRepository utilityBillRepo;

    @Autowired
    private BoardingRepository boardingRepo;

    // ----------------------------------------
    // OWNER: ADD / UPDATE UTILITY BILL
    // ----------------------------------------
    public UtilityBillResponseDTO saveOrUpdate(Long ownerId, UtilityBillCreateDTO dto) {

        Boarding boarding = boardingRepo.findById(dto.getBoardingId())
                .orElseThrow(() -> new RuntimeException("Boarding not found"));

        if (!boarding.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("You are not the owner of this boarding");
        }

        UtilityBill bill = utilityBillRepo
                .findByBoarding_IdAndMonth(dto.getBoardingId(), dto.getMonth())
                .orElse(new UtilityBill());

        bill.setBoarding(boarding);
        bill.setMonth(dto.getMonth());
        bill.setElectricityAmount(dto.getElectricityAmount());
        bill.setWaterAmount(dto.getWaterAmount());

        utilityBillRepo.save(bill);

        return UtilityBillMapper.toDTO(bill);
    }

    // ----------------------------------------
    // OWNER: VIEW ALL UTILITY BILLS
    // ----------------------------------------
    public List<UtilityBillResponseDTO> getForOwner(Long ownerId) {

        return utilityBillRepo.findByBoarding_Owner_Id(ownerId)
                .stream()
                .map(UtilityBillMapper::toDTO)
                .toList();
    }

    // ----------------------------------------
    // VIEW UTILITY BILLS FOR A BOARDING
    // ----------------------------------------
    public List<UtilityBillResponseDTO> getForBoarding(Long boardingId) {

        return utilityBillRepo.findByBoarding_Id(boardingId)
                .stream()
                .map(UtilityBillMapper::toDTO)
                .toList();
    }
}
