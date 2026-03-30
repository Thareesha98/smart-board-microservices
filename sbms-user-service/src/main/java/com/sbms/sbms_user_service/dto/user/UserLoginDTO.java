package com.sbms.sbms_user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User login request payload")
public class UserLoginDTO {

    @Schema(
        description = "Registered user email address",
        example = "student@gmail.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Schema(
        description = "User account password",
        example = "StrongPassword@123",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}
