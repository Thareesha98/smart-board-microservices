package com.sbms.sbms_user_service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Refresh token request payload")
public class RefreshTokenRequest {

    @Schema(
        description = "Valid refresh token issued during login",
        example = "b0f1e7a0-9c64-4e0c-9f3e-92c4b0d6cabc",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;
}
