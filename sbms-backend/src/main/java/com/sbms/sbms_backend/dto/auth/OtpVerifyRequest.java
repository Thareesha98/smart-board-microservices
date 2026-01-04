package com.sbms.sbms_backend.dto.auth;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String email;
    private String otp;
}