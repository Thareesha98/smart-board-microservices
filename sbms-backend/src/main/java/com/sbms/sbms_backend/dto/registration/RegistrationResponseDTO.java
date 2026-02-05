package com.sbms.sbms_backend.dto.registration;

import java.math.BigDecimal;

import com.sbms.sbms_backend.model.enums.RegistrationStatus;
import lombok.Data;

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
    
    private String paymentMethod;       // CARD / BANK_SLIP / CASH
    private String paymentSlipUrl;  
    
    private String agreementPdfPath;


}
