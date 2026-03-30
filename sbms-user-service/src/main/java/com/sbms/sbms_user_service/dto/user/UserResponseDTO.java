package com.sbms.sbms_user_service.dto.user;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

import com.sbms.sbms_user_service.enums.Gender;
import com.sbms.sbms_user_service.enums.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "User response object returned after authentication")
public class UserResponseDTO {

    @Schema(example = "12")
    private Long id;

    @Schema(example = "Kamal Perera")
    private String fullName;

    @Schema(example = "kamal@gmail.com")
    private String email;

    @Schema(example = "0771234567")
    private String phone;

    @Schema(
        description = "Profile image URL stored in S3",
        example = "https://s3.amazonaws.com/sbms/profile.jpg"
    )
    private String profileImageUrl;

    @Schema(example = "Colombo, Sri Lanka")
    private String address;

    @Schema(example = "MALE")
    private Gender gender;

    @Schema(example = "STUDENT")
    private UserRole role;

    // -------- TECHNICIAN ONLY --------

    @Schema(example = "Galle")
    private String city;

    @Schema(example = "Southern")
    private String province;

    @Schema(example = "1500.00")
    private BigDecimal basePrice;

    @Schema(example = "[\"PLUMBING\", \"ELECTRICAL\"]")
    private List<String> skills;

    @Schema(example = "4.5")
    private BigDecimal technicianAverageRating;

    @Schema(example = "25")
    private Integer technicianTotalJobs;

    // -------- OWNER ONLY --------

    @Schema(
        description = "True if owner account is verified by admin",
        example = "true"
    )
    private boolean verifiedOwner;

    @Schema(
        description = "Active subscription ID (owners only)",
        example = "2"
    )
    private Integer subscription_id;

    @Schema(
        description = "Owner bank account number",
        example = "1234567890"
    )
    private String accNo;

    // -------- STUDENT ONLY --------

    @Schema(
        description = "Student university name",
        example = "University of Moratuwa"
    )
    private String studentUniversity;
    
    
    
    
}