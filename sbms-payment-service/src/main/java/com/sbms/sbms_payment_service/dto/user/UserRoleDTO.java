package com.sbms.sbms_payment_service.dto.user;

import com.sbms.sbms_notification_service.model.enums.UserRole;

import lombok.Data;

@Data
public class UserRoleDTO {
    private Long userId;
    private UserRole role;
}
