package com.sbms.sbms_monolith.dto.admin;

import com.sbms.sbms_user_service.enums.Status;

import lombok.Data;

@Data
public class BoardingApprovalDTO {
    private Status decision; // APPROVED / REJECTED
    private String reason;
}
