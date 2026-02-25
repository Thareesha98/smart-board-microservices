package com.sbms.sbms_registration_service.dto;

import lombok.Data;

import java.math.BigDecimal;

import com.sbms.sbms_registration_service.enums.RegistrationStatus;

@Data
public class RegistrationResponseDTO {

    private Long id;

    private Long boardingId;
    private String boardingTitle;

    private Long studentId;
    private String studentName;
    private String studentEmail;

    private int numberOfStudents;

    private RegistrationStatus status;

    private String studentNote;
    private String ownerNote;

    private BigDecimal keyMoney;
    private BigDecimal monthlyPrice;

    private boolean keyMoneyPaid;
}
