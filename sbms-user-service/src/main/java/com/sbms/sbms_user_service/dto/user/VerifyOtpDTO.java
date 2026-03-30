package com.sbms.sbms_user_service.dto.user;

import lombok.Data;

@Data
public class VerifyOtpDTO {
    private String email;
    private String otp;
}
