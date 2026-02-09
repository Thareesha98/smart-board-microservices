package com.sbms.sbms_report_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_report_service.model.dto.ReportCreateDTO;
import com.sbms.sbms_report_service.model.dto.ReportResponseDTO;
import com.sbms.sbms_report_service.service.ReportService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // ======================
    // 1. CREATE REPORT
    // =========================
    @PostMapping
    public ResponseEntity<ReportResponseDTO> createReport(
            @RequestHeader("X-User-Id") Long reporterId,
            @RequestBody ReportCreateDTO dto
    ) {
        return ResponseEntity.ok(
                reportService.createReport(reporterId, dto)
        );
    }

    // =========================
    // 2. MY SENT REPORTS
    // =========================
    @GetMapping("/my")
    public ResponseEntity<List<ReportResponseDTO>> getMyReports(
            @RequestHeader("X-User-Id") Long reporterId
    ) {
        return ResponseEntity.ok(
                reportService.getSentReports(reporterId)
        );
    }

    // =========================
    // 3. USER HISTORY
    // =========================
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ReportResponseDTO>> getUserHistory(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                reportService.getUserHistory(userId)
        );
    }

    // =========================
    // 4. ADMIN: GET ALL
    // =========================
    @GetMapping("/admin")
    public ResponseEntity<List<ReportResponseDTO>> getAllReports(
            @RequestHeader("X-User-Role") String role
    ) {
        enforceAdmin(role);
        return ResponseEntity.ok(
                reportService.getAllReports()
        );
    }

    // =========================
    // 5. ADMIN: INVESTIGATE
    // =========================
    @PutMapping("/{id}/investigate")
    public ResponseEntity<ReportResponseDTO> investigate(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role
    ) {
        enforceAdmin(role);
        return ResponseEntity.ok(
                reportService.startInvestigation(id)
        );
    }

    // =========================
    // 6. ADMIN: RESOLVE
    // =========================
    @PutMapping("/{id}/resolve")
    public ResponseEntity<ReportResponseDTO> resolve(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestBody Map<String, String> body
    ) {
        enforceAdmin(role);

        return ResponseEntity.ok(
                reportService.resolveReport(
                        id,
                        body.get("text"),
                        body.get("action"),
                        body.get("duration")
                )
        );
    }

    // =========================
    // 7. ADMIN: DISMISS
    // =========================
    @PutMapping("/{id}/dismiss")
    public ResponseEntity<ReportResponseDTO> dismiss(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestBody Map<String, String> body
    ) {
        enforceAdmin(role);

        return ResponseEntity.ok(
                reportService.dismissReport(
                        id,
                        body.get("text")
                )
        );
    }

    // =========================
    // INTERNAL
    // =========================
    private void enforceAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Admin access required");
        }
    }
}
