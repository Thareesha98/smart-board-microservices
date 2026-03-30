package com.sbms.sbms_user_service.dto.user;


import com.sbms.sbms_user_service.enums.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User profile update request payload")
public class UserUpdateDTO {

    @Schema(example = "Kamal Perera")
    private String fullName;

    @Schema(example = "0771234567")
    private String phone;

    @Schema(example = "Colombo, Sri Lanka")
    private String address;

    @Schema(example = "MALE")
    private Gender gender;

    // -------- STUDENT FIELDS --------
    @Schema(example = "University of Colombo")
    private String studentUniversity;

    // -------- OWNER FIELDS --------
    @Schema(example = "200012345678")
    private String nicNumber;

    @Schema(example = "1234567890")
    private String accNo;
}
