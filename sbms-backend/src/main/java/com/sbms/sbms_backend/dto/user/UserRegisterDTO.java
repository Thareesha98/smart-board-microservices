package com.sbms.sbms_backend.dto.user;

import com.sbms.sbms_backend.model.enums.Gender;
import com.sbms.sbms_backend.model.enums.UserRole;
import lombok.Data;

@Data
public class UserRegisterDTO {

    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private Gender gender;

    private UserRole role;   // STUDENT / OWNER

    // OWNER fields
    private String nicNumber;
    private String accNo;

    // STUDENT fields
    private String studentUniversity;
}

