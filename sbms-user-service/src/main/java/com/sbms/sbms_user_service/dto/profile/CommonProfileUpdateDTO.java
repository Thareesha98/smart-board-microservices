package com.sbms.sbms_user_service.dto.profile;


import lombok.Data;

@Data
public class CommonProfileUpdateDTO {

    private String fullName;
    private String phone;
    private String profileImageUrl;
}
