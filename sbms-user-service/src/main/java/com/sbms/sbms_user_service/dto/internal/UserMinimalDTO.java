package com.sbms.sbms_user_service.dto.internal;

import com.sbms.sbms_user_service.enums.UserRole;

import lombok.Data;

@Data
public class UserMinimalDTO {

    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean verifiedOwner;
}
