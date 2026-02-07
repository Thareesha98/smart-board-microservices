package com.sbms.sbms_backend.dto.user;

import com.sbms.sbms_backend.model.enums.UserRole;

import lombok.Data;

@Data
public class UserMinimalDTO {

    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean verifiedOwner;
}