package com.sbms.sbms_backend.dto.user;

import lombok.Data;

import java.util.List;

import com.sbms.sbms_backend.model.enums.Gender;
import com.sbms.sbms_backend.model.enums.UserRole;

@Data
public class UserSnapshotDTO {

    private Long id;

    private String fullName;
    private String email;
    private String phone;
    private String profileImageUrl;

    private Gender gender;
    private String nicNumber;
    private String address;

    private UserRole role;

    // OWNER FIELDS
    private boolean verifiedOwner;
    private int subscriptionId;
    private String accNo;

    // STUDENT FIELDS
    private String studentUniversity;

    // REFERENCES (IDs ONLY)
    private List<Long> boardings;

    // METADATA
    private boolean active;
}
