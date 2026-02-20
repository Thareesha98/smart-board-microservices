package com.sbms.sbms_payment_service.dto.user;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String email;
    private String password;
}
