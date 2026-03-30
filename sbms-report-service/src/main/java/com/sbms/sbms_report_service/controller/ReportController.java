package com.sbms.sbms_report_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_report_service.model.dto.ReportCreateDTO;
import com.sbms.sbms_report_service.model.dto.ReportResponseDTO;
import com.sbms.sbms_report_service.service.ReportService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // 1. Create Report (MATCHED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReportResponseDTO> create(
            @RequestHeader("X-User-Id") Long reporterId,
            @ModelAttribute ReportCreateDTO dto
    ) {
        // reporterId comes from Gateway/Auth header, dto contains the rest
        return ResponseEntity.ok(reportService.createReport(reporterId, dto));
    }

    // 2. Get My Sent Reports (MATCHED: /sent/{userId})
    // Note: Even though we have X-User-Id, we keep PathVariable to match Monolith URL
    @GetMapping("/sent/{userId}")
    public ResponseEntity<List<ReportResponseDTO>> getSentReports(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getSentReports(userId));
    }

    // 3. Get Profile History (MATCHED: /history/{userId})
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ReportResponseDTO>> getUserHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getUserHistory(userId));
    }

    // 4. Admin: All (MATCHED: /admin/all)
    @GetMapping("/admin/all")
    public ResponseEntity<List<ReportResponseDTO>> getAllReports(
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        enforceAdmin(role);
        return ResponseEntity.ok(reportService.getAllReports());
    }

    // 5. Admin: Investigate (MATCHED)
    @PutMapping("/{id}/investigate")
    public ResponseEntity<ReportResponseDTO> investigate(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        enforceAdmin(role);
        return ResponseEntity.ok(reportService.startInvestigation(id));
    }

    // 6. Admin: Resolve (MATCHED)
    @PutMapping("/{id}/resolve")
    public ResponseEntity<ReportResponseDTO> resolve(
            @PathVariable Long id, 
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestBody Map<String, String> body
    ) {
        enforceAdmin(role);
        return ResponseEntity.ok(reportService.resolveReport(
                id, body.get("text"), body.get("action"), body.get("duration"))
        );
    }

    // 7. Admin: Dismiss (MATCHED)
    @PutMapping("/{id}/dismiss")
    public ResponseEntity<ReportResponseDTO> dismiss(
            @PathVariable Long id, 
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestBody Map<String, String> body
    ) {
        enforceAdmin(role);
        return ResponseEntity.ok(reportService.dismissReport(id, body.get("text")));
    }

    private void enforceAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            // In microservices, the Gateway usually handles this, 
            // but this is a good safety check.
            throw new RuntimeException("Admin access required");
        }
    }
}