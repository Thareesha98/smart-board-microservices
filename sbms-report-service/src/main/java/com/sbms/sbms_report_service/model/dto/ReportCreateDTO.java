package com.sbms.sbms_report_service.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReportCreateDTO {

    private String reportTitle;
    private String reportDescription;
    private String type;
    private String severity;
    private String boarding;             // Name of the boarding

    private Long senderId;         // The Owner/Student ID
    private Long reportedUserId;   // (Optional) The Student ID being reported
    private String reportedPersonName;

    private Boolean allowContact;
    private LocalDate incidentDate;


    // The files uploaded by user
    private List<MultipartFile> evidence;
}
