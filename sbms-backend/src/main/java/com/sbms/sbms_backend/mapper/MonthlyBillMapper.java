package com.sbms.sbms_backend.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import com.sbms.sbms_backend.client.BoardingClient;
import com.sbms.sbms_backend.dto.billing.MonthlyBillResponseDTO;
import com.sbms.sbms_backend.model.MonthlyBill;
import com.sbms.sbms_backend.model.enums.BillDueStatus;

public class MonthlyBillMapper {
	
    // 1. Pass the ownerId in as an argument
    public static MonthlyBillResponseDTO toDTO(MonthlyBill b, 
            BillDueStatus dueStatus,
            int dueInDays,
            Long ownerId // Passed from Service
        ) {
    	
        MonthlyBillResponseDTO dto = new MonthlyBillResponseDTO();

        dto.setId(b.getId());
        dto.setStudentId(b.getStudent().getId());
        dto.setStudentName(b.getStudent().getFullName());
        dto.setBoardingId(b.getBoardingId());
        
        dto.setOwnerId(ownerId); // Set the passed ID

        dto.setMonth(b.getMonth());
        dto.setBoardingFee(b.getBoardingFee());
        dto.setElectricityFee(b.getElectricityFee());
        dto.setWaterFee(b.getWaterFee());
        dto.setTotalAmount(b.getTotalAmount());
        dto.setStatus(b.getStatus());
        dto.setDueDate(b.getDueDate());
        dto.setDueStatus(dueStatus);
        dto.setDueInDays(dueInDays);

        return dto;
    }
}