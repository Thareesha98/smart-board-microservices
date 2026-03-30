package com.sbms.sbms_user_service.dto.auth;

import lombok.Data;

import com.sbms.sbms_user_service.dto.user.UserResponseDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "JWT authentication response")
public class JwtAuthResponse {

    @Schema(
        description = "JWT access token used for authenticating API requests",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String token;

    @Schema(
        description = "Refresh token stored in database and used to generate new JWTs",
        example = "b0f1e7a0-9c64-4e0c-9f3e-92c4b0d6cabc"
    )
    private String refreshToken;

    @Schema(
        description = "Token type to be used in Authorization header",
        example = "Bearer",
        defaultValue = "Bearer"
    )
    private String tokenType = "Bearer";

    @Schema(
        description = "Authenticated user details"
    )
    private UserResponseDTO user;
}
