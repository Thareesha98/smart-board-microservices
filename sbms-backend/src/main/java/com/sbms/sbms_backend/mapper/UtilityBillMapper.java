package com.sbms.sbms_backend.mapper;


import com.sbms.sbms_backend.dto.billing.CreateUtilityBillDTO;
import com.sbms.sbms_backend.dto.billing.UtilityBillResponseDTO;
import com.sbms.sbms_backend.model.UtilityBill;

public class UtilityBillMapper {

    public static UtilityBill toEntity(CreateUtilityBillDTO dto, Long boardingId) {

        UtilityBill bill = new UtilityBill();
        bill.setBoardingId(boardingId);
        bill.setMonth(dto.getMonth());
        bill.setElectricityAmount(dto.getElectricityAmount());
        bill.setWaterAmount(dto.getWaterAmount());

        return bill;
    }

    public static UtilityBillResponseDTO toDTO(UtilityBill bill) {

        UtilityBillResponseDTO dto = new UtilityBillResponseDTO();
        dto.setId(bill.getId());
        dto.setBoardingId(bill.getBoardingId());
      //  dto.setBoardingName(bill.getBoardingName());
        dto.setMonth(bill.getMonth());
        dto.setElectricityAmount(bill.getElectricityAmount());
        dto.setWaterAmount(bill.getWaterAmount());

        return dto;
    }
}
