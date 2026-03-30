package com.sbms.sbms_user_service.dto.internal;

import com.sbms.sbms_user_service.enums.UserRole;

import lombok.Data;

@Data
public class UserRoleDTO {
    private Long userId;
    private UserRole role;
}
