package com.sbms.sbms_payment_service.dto.user;

import lombok.Data;

@Data
public class OwnerProfileDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;

    private String address;
    private String profileImageUrl;
    private String nicNumber;
    private boolean verifiedOwner;

    private Integer subscription_id;
    private String accNo;

    private int totalBoardings;   // owner boardings count
}
