package com.sbms.sbms_report_service.mapper;

import com.sbms.sbms_report_service.model.Report;
import com.sbms.sbms_report_service.model.dto.AdminReportResponseDTO;

public class AdminReportMapper {

    public static AdminReportResponseDTO toDTO(Report r) {
        AdminReportResponseDTO dto = new AdminReportResponseDTO();

        dto.setId(r.getId());
        dto.setTitle(r.getTitle());
        dto.setDescription(r.getDescription());
        dto.setType(r.getType());
        dto.setSeverity(r.getSeverity());
        dto.setStatus(r.getStatus());
        dto.setSubmissionDate(r.getSubmissionDate());
        dto.setIncidentDate(r.getIncidentDate());
        dto.setBoardingId(r.getBoardingId());

        // Set IDs only. 
        // IMPRTANT: The names (senderName, reportedUserName) MUST be set in the 
        // Service layer using your UserClient (WebClient).
        dto.setSenderId(r.getReporterId());
        dto.setReportedUserId(r.getReportedUserId());

        dto.setAllowContact(r.isAllowContact());
        dto.setResolutionDetails(r.getResolutionDetails());
        dto.setDismissalReason(r.getDismissalReason());
        dto.setActionTaken(r.getActionTaken());
        dto.setActionDuration(r.getActionDuration());
        dto.setResolvedAt(r.getResolvedAt());
        dto.setEvidence(r.getEvidence());

        return dto;
    }
}