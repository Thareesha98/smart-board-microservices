package com.sbms.sbms_backend.dto.user;

import com.sbms.sbms_backend.model.enums.UserRole;

import lombok.Data;

@Data
public class UserRoleDTO {
    private Long userId;
    private UserRole role;
}
