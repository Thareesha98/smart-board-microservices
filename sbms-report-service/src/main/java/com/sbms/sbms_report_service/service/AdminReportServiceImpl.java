package com.sbms.sbms_report_service.service;


import com.sbms.sbms_report_service.client.UserClient;
import com.sbms.sbms_report_service.mapper.AdminReportMapper;
import com.sbms.sbms_report_service.model.Report;
import com.sbms.sbms_report_service.model.dto.AdminReportResponseDTO;
import com.sbms.sbms_report_service.model.dto.ReportDecisionDTO;
import com.sbms.sbms_report_service.model.dto.UserMinimalDTO;
import com.sbms.sbms_report_service.model.enums.ReportStatus;
import com.sbms.sbms_report_service.repository.ReportRepository;
import com.sbms.sbms_report_service.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final ReportRepository reportRepository;
    private final UserClient userClient;

    @Override
    public List<AdminReportResponseDTO> getReports(ReportStatus status) {
        List<Report> reports = (status == null) 
                ? reportRepository.findAll() 
                : reportRepository.findByStatus(status);

        return reports.stream()
                .map(report -> {
                    AdminReportResponseDTO dto = AdminReportMapper.toDTO(report);
                    enrichWithUserNames(dto, report);
                    return dto;
                })
                .toList();
    }

    @Override
    public void resolveReport(Long reportId, ReportDecisionDTO dto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ReportStatus.RESOLVED);
        report.setResolutionDetails(dto.getResolutionDetails());
        report.setActionTaken(dto.getActionTaken());
        report.setActionDuration(dto.getActionDuration());
        report.setResolvedAt(LocalDateTime.now());

        reportRepository.save(report);
    }

    @Override
    public void dismissReport(Long reportId, ReportDecisionDTO dto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ReportStatus.DISMISSED);
        report.setDismissalReason(dto.getDismissalReason());
        report.setResolvedAt(LocalDateTime.now());

        reportRepository.save(report);
    }

    // Helper method to talk to User Service
    private void enrichWithUserNames(AdminReportResponseDTO dto, Report report) {
        if (report.getReporterId() != null) {
            UserMinimalDTO sender = userClient.getUserMinimal(report.getReporterId());
            if (sender != null) dto.setSenderName(sender.getFullName());
        }

        if (report.getReportedUserId() != null) {
            UserMinimalDTO reported = userClient.getUserMinimal(report.getReportedUserId());
            if (reported != null) dto.setReportedUserName(reported.getFullName());
        }
    }
}