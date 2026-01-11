package com.sbms.sbms_backend.mapper;

import java.math.BigDecimal;

import com.sbms.sbms_backend.dto.registration.RegistrationResponseDTO;
import com.sbms.sbms_backend.model.Registration;

public class RegistrationMapper {

    public static RegistrationResponseDTO toDTO(
            Registration r,
            String boardingTitle,
            BigDecimal keyMoney,
            BigDecimal monthlyPrice
    ) {

        RegistrationResponseDTO dto = new RegistrationResponseDTO();

        // Registration
        dto.setId(r.getId());
        dto.setBoardingId(r.getBoardingId());
        dto.setBoardingTitle(boardingTitle);

        // Student
        dto.setStudentId(r.getStudent().getId());
        dto.setStudentName(r.getStudent().getFullName());
        dto.setStudentEmail(r.getStudent().getEmail());

        // Registration info
        dto.setNumberOfStudents(r.getNumberOfStudents());
        dto.setStatus(r.getStatus());
        dto.setStudentNote(r.getStudentNote());
        dto.setOwnerNote(r.getOwnerNote());

        // Payment
        dto.setKeyMoney(keyMoney);
        dto.setMonthlyPrice(monthlyPrice);
        dto.setKeyMoneyPaid(r.isKeyMoneyPaid());

        return dto;
    }
}
