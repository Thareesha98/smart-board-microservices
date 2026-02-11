package com.sbms.sbms_report_service.model.dto;

import com.sbms.sbms_report_service.model.enums.ReportSeverity;

import lombok.Data;

@Data
public class ReportResponseDTO {
    private Long id;
    private String title;
    private String description;

    // Frontend Display Fields
    private String status;
    private String type;
    private ReportSeverity priority;
    private String date;

    // Context
    private Long boardingId;      
    private String student;

    // Admin Actions
    private String adminResponse;
    private String actionTaken;
    private String actionDuration;

    // Evidence & Meta
    private int evidenceCount;
    private EvidenceDTO evidence;

    // Nested Objects
    private UserDTO sender;
    private UserDTO reportedUser;

    @Data
    public static class EvidenceDTO {
        private String type; // "image" or "document"
        private String url;
        private String name;
    }

    @Data
    public static class UserDTO {
        private Long id;
        private String name;
        private String avatar;
        private String role;
    }

}
