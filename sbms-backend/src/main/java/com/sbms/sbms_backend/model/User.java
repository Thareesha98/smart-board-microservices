package com.sbms.sbms_backend.model;

import com.sbms.sbms_backend.common.BaseEntity;
import com.sbms.sbms_backend.model.enums.Gender;
import com.sbms.sbms_backend.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    // -----------------------
    // BASIC USER INFO
    // -----------------------
    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    private String profileImageUrl;  // stored in S3 later
    
    private Gender gender;
    
    private String nicNumber;
    
    private String address;


    // -----------------------
    // ROLE: STUDENT / OWNER / ADMIN
    // -----------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;


    // -----------------------
    // OWNER SPECIFIC FIELDS
    // (these will be used only if role == OWNER)
    // -----------------------
          // National ID (optional)
                // Owner home/business address

    private boolean verifiedOwner = true;  // For admin approval later
    private int subscription_id;
    private String accNo;
    

    // -----------------------
    // STUDENT SPECIFIC FIELDS
    // (these will be used only if role == STUDENT)
    // -----------------------
    private String studentUniversity;



    @ElementCollection
    @CollectionTable(
        name = "owner_boardings",
        joinColumns = @JoinColumn(name = "owner_id")
    )
    @Column(name = "boarding_id")
    private List<Long> boardings;   
}
