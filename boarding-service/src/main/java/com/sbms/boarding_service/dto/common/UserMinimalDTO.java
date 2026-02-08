package com.sbms.boarding_service.dto.common;

import com.sbms.boarding_service.model.enums.UserRole;

import lombok.Data;

@Data
public class UserMinimalDTO {

    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean verifiedOwner;
}