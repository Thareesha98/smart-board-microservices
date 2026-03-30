package com.sbms.sbms_monolith.dto.admin;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AdminUserDashboardDTO {

    private long totalUsers;
    private long totalStudents;
    private long totalOwners;
    private long totalBoardings;
    private long pendingReports;
    private long  unverifiedOwners;
}

