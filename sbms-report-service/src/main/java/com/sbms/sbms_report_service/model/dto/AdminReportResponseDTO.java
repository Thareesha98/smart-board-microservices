package com.sbms.sbms_report_service.model.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.sbms.sbms_report_service.model.enums.ReportSeverity;
import com.sbms.sbms_report_service.model.enums.ReportStatus;
import com.sbms.sbms_report_service.model.enums.ReportType;



@Data
public class AdminReportResponseDTO {

    private Long id;
    private String title;
    private String description;

    private ReportType type;
    private ReportSeverity severity;
    private ReportStatus status;

    private LocalDateTime submissionDate;
    private LocalDate incidentDate;

    private String boardingName;

    private Long senderId;
    private String senderName;

    private Long reportedUserId;
    private String reportedUserName;

    private boolean allowContact;

    private String resolutionDetails;
    private String dismissalReason;
    private String actionTaken;
    private String actionDuration;

    private LocalDateTime resolvedAt;

    private List<String> evidence;
}
