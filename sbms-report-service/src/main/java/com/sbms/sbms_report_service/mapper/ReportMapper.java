package com.sbms.sbms_report_service.mapper;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sbms.sbms_report_service.model.Report;
import com.sbms.sbms_report_service.model.dto.ReportCreateDTO;
import com.sbms.sbms_report_service.model.dto.ReportResponseDTO;
import com.sbms.sbms_report_service.model.enums.ReportSeverity;
import com.sbms.sbms_report_service.model.enums.ReportStatus;
import com.sbms.sbms_report_service.model.enums.ReportType;

public class ReportMapper {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ======================
    // ENTITY → RESPONSE
    // ======================
    public static ReportResponseDTO toDTO(Report r) {
        ReportResponseDTO dto = new ReportResponseDTO();

        dto.setId(r.getId());
        dto.setTitle(r.getTitle());
        dto.setDescription(r.getDescription());
        dto.setType(r.getType() != null ? r.getType().name() : "OTHER");
        dto.setPriority(r.getSeverity());
        dto.setStatus(mapStatus(r.getStatus()));

        if (r.getSubmissionDate() != null) {
            dto.setDate(r.getSubmissionDate().format(DATE_FMT));
        }

        dto.setBoardingId(r.getBoardingId());
        dto.setAdminResponse(r.getResolutionDetails());
        dto.setActionTaken(r.getActionTaken());
        dto.setActionDuration(r.getActionDuration());

        // Initialize nested UserDTOs with IDs
        if (r.getReporterId() != null) {
            ReportResponseDTO.UserDTO sender = new ReportResponseDTO.UserDTO();
            sender.setId(r.getReporterId());
            dto.setSender(sender);
        }

        if (r.getReportedUserId() != null) {
            ReportResponseDTO.UserDTO reported = new ReportResponseDTO.UserDTO();
            reported.setId(r.getReportedUserId());
            dto.setReportedUser(reported);
        }

        mapEvidence(r, dto);

        return dto;
    }

    // ======================
    // CREATE DTO → ENTITY
    // ======================
    // Note: We added 'evidenceUrls' as a parameter because the DTO has MultipartFiles
    // but the Entity needs String URLs after they are uploaded to S3/Cloudinary.
    public static Report toEntity(ReportCreateDTO dto, Long reporterId, List<String> evidenceUrls) {
        Report r = new Report();

        r.setTitle(dto.getReportTitle());
        r.setDescription(dto.getReportDescription());
        r.setReporterId(reporterId);
        r.setReportedUserId(dto.getReportedUserId());
        
        // Mapping 'boarding' string from DTO to 'boardingName' in Entity

        r.setBoardingId(dto.getBoardingId());
        r.setIncidentDate(dto.getIncidentDate());
        r.setSubmissionDate(LocalDateTime.now());
        r.setAllowContact(Boolean.TRUE.equals(dto.getAllowContact()));

        r.setType(parseType(dto.getType()));
        r.setSeverity(parseSeverity(dto.getSeverity()));
        r.setStatus(ReportStatus.PENDING);

        // Evidence is passed in from the service after upload
        if (evidenceUrls != null) {
            r.setEvidence(evidenceUrls);
        }

        return r;
    }

    // ======================
    // HELPERS
    // ======================
    private static String mapStatus(ReportStatus status) {
        if (status == null) return "New";
        return switch (status) {
            case PENDING -> "New";
            case INVESTIGATING -> "In Progress";
            case RESOLVED -> "Resolved";
            case DISMISSED -> "Dismissed";
            default -> "In Progress";
        };
    }

    private static ReportType parseType(String type) {
        try {
            return ReportType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            return ReportType.OTHER;
        }
    }

    private static ReportSeverity parseSeverity(String severity) {
        try {
            return ReportSeverity.valueOf(severity.toUpperCase());
        } catch (Exception e) {
            return ReportSeverity.LOW;
        }
    }

    private static void mapEvidence(Report r, ReportResponseDTO dto) {
        if (r.getEvidence() == null || r.getEvidence().isEmpty()) {
            dto.setEvidenceCount(0);
            return;
        }

        dto.setEvidenceCount(r.getEvidence().size());
        String url = r.getEvidence().get(0);

        ReportResponseDTO.EvidenceDTO ev = new ReportResponseDTO.EvidenceDTO();
        ev.setUrl(url);
        ev.setName("Evidence Attachment");
        ev.setType(url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".png") ? "image" : "document");

        dto.setEvidence(ev);
    }
}