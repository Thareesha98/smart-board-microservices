package com.sbms.sbms_report_service.model.dto;

import com.sbms.sbms_report_service.model.enums.UserRole;

import lombok.Data;

@Data
public class UserMinimalDTO {

    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean verifiedOwner;
}