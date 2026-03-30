package com.sbms.sbms_user_service.dto.profile;


import com.sbms.sbms_user_service.enums.Gender;

import lombok.Data;

@Data
public class StudentProfileUpdateDTO {

    private String fullName;
    private String phone;
    private String profileImageUrl;

    private String studentUniversity;

    private String address;
    private Gender gender;
    private String dob;
    private String emergencyContact;
    private String studentId;
}
