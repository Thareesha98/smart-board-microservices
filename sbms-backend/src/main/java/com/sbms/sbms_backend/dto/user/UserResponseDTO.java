package com.sbms.sbms_backend.dto.user;

import com.sbms.sbms_backend.model.enums.Gender;
import com.sbms.sbms_backend.model.enums.UserRole;
import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;

    private String fullName;
    private String email;
    private String phone;
    private String profileImageUrl;
    private String address;
    private Gender gender;

    private UserRole role;

    // Owner only
    private boolean verifiedOwner;
    private Integer subscription_id;
    private String accNo;

    // Student only
    private String studentUniversity;
}
