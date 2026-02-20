package com.sbms.sbms_payment_service.dto.user;

import com.sbms.sbms_notification_service.model.enums.UserRole;

import lombok.Data;

@Data
public class AdminUserDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;

    private UserRole role;

    private boolean verifiedOwner;
    private Integer subscription_id;
}
