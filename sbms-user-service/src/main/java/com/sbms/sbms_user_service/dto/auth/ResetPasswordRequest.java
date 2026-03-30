package com.sbms.sbms_user_service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Password reset request payload")
public class ResetPasswordRequest {

    @Schema(
        description = "Registered user email",
        example = "user@gmail.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Schema(
        description = "OTP received via email",
        example = "654321"
    )
    private String otp;

    @Schema(
        description = "New password to set",
        example = "NewStrongPassword@123"
    )
    private String newPassword;
}
