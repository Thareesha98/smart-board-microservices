package com.sbms.sbms_user_service.dto.user;

import lombok.Data;

import com.sbms.sbms_user_service.enums.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "User summary used in admin dashboards")
public class AdminUserDTO {

    @Schema(example = "5")
    private Long id;

    @Schema(example = "Nimal Silva")
    private String fullName;

    @Schema(example = "nimal@gmail.com")
    private String email;

    @Schema(example = "0719876543")
    private String phone;

    @Schema(example = "OWNER")
    private UserRole role;

    @Schema(
        description = "Whether owner is verified",
        example = "false"
    )
    private boolean verifiedOwner;

    @Schema(
        description = "Subscription ID if user is an owner",
        example = "1"
    )
    private Integer subscription_id;
}
