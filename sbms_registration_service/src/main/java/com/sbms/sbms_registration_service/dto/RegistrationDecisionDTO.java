package com.sbms.sbms_registration_service.dto;

import com.sbms.sbms_registration_service.enums.RegistrationStatus;

import lombok.Data;

@Data
public class RegistrationDecisionDTO {

    // APPROVED or DECLINED
    private RegistrationStatus status;

    private String ownerNote;
}
