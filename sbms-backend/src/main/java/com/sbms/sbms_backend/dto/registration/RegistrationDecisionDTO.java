package com.sbms.sbms_backend.dto.registration;

import com.sbms.sbms_backend.model.enums.RegistrationStatus;
import lombok.Data;

@Data
public class RegistrationDecisionDTO {

    private RegistrationStatus status; // APPROVED or DECLINED

    private String ownerNote;
    
    private String ownerSignatureBase64;
    private boolean approveWithPendingPayment;
}
