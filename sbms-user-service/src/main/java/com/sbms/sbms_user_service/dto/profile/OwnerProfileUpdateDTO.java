package com.sbms.sbms_user_service.dto.profile;


import lombok.Data;

@Data
public class OwnerProfileUpdateDTO {

    private String fullName;
    private String phone;
    private String address;
    private String profileImageUrl;
    

    private String accNo;
}
