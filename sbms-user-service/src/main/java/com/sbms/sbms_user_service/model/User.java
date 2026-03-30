package com.sbms.sbms_user_service.model;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import com.sbms.sbms_user_service.common.BaseEntity;
import com.sbms.sbms_user_service.enums.Gender;
import com.sbms.sbms_user_service.enums.MaintenanceIssueType;
import com.sbms.sbms_user_service.enums.UserRole;

@Data
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    private String profileImageUrl;

    private Gender gender;

    private String nicNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private boolean verifiedOwner = true;
    private int subscription_id;
    private String accNo;

    private String studentUniversity;

    @ElementCollection
    @CollectionTable(
            name = "owner_boardings",
            joinColumns = @JoinColumn(name = "owner_id")
    )
    @Column(name = "boarding_id")
    private List<Long> boardings;
    
    private String dob;
    private String emergencyContact;
    
    
    
    
    
    
    
    
    
    
    
    
    
    private String province;

    private String city;

    private Double basePrice;

    @ElementCollection(targetClass = MaintenanceIssueType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "technician_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "skill")
    private List<MaintenanceIssueType> skills; // CHANGED from String to MaintenanceIssueType

    @Column(precision = 3, scale = 1)
    private BigDecimal technicianAverageRating = BigDecimal.ZERO;

    private Integer technicianTotalJobs = 0;

}
