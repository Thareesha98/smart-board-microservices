package com.sbms.sbms_user_service.dto.email;

import lombok.Data;

@Data
public class SendResetPasswordEmailRequest {
    private String email;
    private String token;
}
