package com.sbms.sbms_user_service.dto.email;

import lombok.Data;

@Data
public class SendOtpEmailRequest {
    private String email;
    private String otp;
}