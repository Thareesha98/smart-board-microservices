package com.sbms.sbms_report_service.controller;


import com.sbms.sbms_report_service.model.dto.AdminReportResponseDTO;
import com.sbms.sbms_report_service.model.dto.ReportDecisionDTO;
import com.sbms.sbms_report_service.model.enums.ReportStatus;
import com.sbms.sbms_report_service.service.AdminReportService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report/admin") // Note the service prefix
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/reports")
    public List<AdminReportResponseDTO> getReports(
            @RequestParam(required = false) ReportStatus status
    ) {
        return adminReportService.getReports(status);
    }

    @PutMapping("/reports/{reportId}/resolve")
    public void resolveReport(
            @PathVariable Long reportId,
            @RequestBody ReportDecisionDTO dto
    ) {
        adminReportService.resolveReport(reportId, dto);
    }

    @PutMapping("/reports/{reportId}/dismiss")
    public void dismissReport(
            @PathVariable Long reportId,
            @RequestBody ReportDecisionDTO dto
    ) {
        adminReportService.dismissReport(reportId, dto);
    }
}