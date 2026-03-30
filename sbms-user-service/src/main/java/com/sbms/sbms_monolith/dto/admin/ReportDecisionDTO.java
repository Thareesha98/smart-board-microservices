package com.sbms.sbms_monolith.dto.admin;


import lombok.Data;


@Data
public class ReportDecisionDTO {

    // Used when RESOLVING
    private String resolutionDetails;
    private String actionTaken;
    private String actionDuration;

    // Used when DISMISSING
    private String dismissalReason;
}
