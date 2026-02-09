package com.sbms.sbms_report_service.service;

import com.sbms.sbms_report_service.model.dto.AdminReportResponseDTO;
import com.sbms.sbms_report_service.model.dto.ReportDecisionDTO;
import com.sbms.sbms_report_service.model.enums.ReportStatus;
import java.util.List;

public interface AdminReportService {
    List<AdminReportResponseDTO> getReports(ReportStatus status);
    void resolveReport(Long reportId, ReportDecisionDTO dto);
    void dismissReport(Long reportId, ReportDecisionDTO dto);
}