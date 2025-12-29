package com.sbms.sbms_backend.dto.user;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String email;
    private String password;
}
