package com.sbms.sbms_user_service.dto.technical;

import java.util.List;

import com.sbms.sbms_user_service.enums.MaintenanceIssueType;

import lombok.Data;

@Data
public class TechnicianCardDTO {

    private Long id;

    private String fullName;

    private String profileImageUrl;

    private String city;

    private Double basePrice;

    private List<MaintenanceIssueType> skills;

    private Double averageRating;

    private Integer totalJobs;
}