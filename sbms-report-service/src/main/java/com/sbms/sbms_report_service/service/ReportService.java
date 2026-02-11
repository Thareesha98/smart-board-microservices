package com.sbms.sbms_report_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sbms.sbms_report_service.mapper.ReportMapper;
import com.sbms.sbms_report_service.model.Report;
import com.sbms.sbms_report_service.model.dto.ReportCreateDTO;
import com.sbms.sbms_report_service.model.dto.ReportResponseDTO;
import com.sbms.sbms_report_service.model.enums.ReportStatus;
import com.sbms.sbms_report_service.repository.ReportRepository;

@Service

public class ReportService {

    @Autowired
    private ReportRepository reportRepo;

    // =========================
    // 1. CREATE REPORT
    // =========================
    public ReportResponseDTO createReport(Long reporterId, ReportCreateDTO dto) {
        
    	if (dto.getBoardingId() == null) {
    	    throw new RuntimeException("Boarding ID is required");
    	}

    	
        // 1. Handle file uploads first
        List<String> evidenceUrls = null;
        if (dto.getEvidence() != null && !dto.getEvidence().isEmpty()) {
            // You should have a FileService to handle this. 
            // For now, I'm calling a placeholder method:
            evidenceUrls = uploadEvidenceFiles(dto.getEvidence());
        }

        // 2. Map DTO to Entity using the new signature we created
        // Pass the reporterId and the generated URLs
        Report report = ReportMapper.toEntity(dto, reporterId, evidenceUrls);

        // 3. Persist to database
        Report saved = reportRepo.save(report);

        // 4. Return DTO (enrichment with names usually happens in 'get' methods)
        return ReportMapper.toDTO(saved);
    }

    /**
     * Placeholder for your file upload logic
     */
    private List<String> uploadEvidenceFiles(List<MultipartFile> files) {
        // This logic would normally call an External Service or FileSystem
        // and return a list of URLs where the files are stored.
        return files.stream()
                .map(file -> "https://storage.sbms.com/evidence/" + file.getOriginalFilename())
                .toList();
    }
    // =========================
    // 2. GET SENT REPORTS
    // =========================
    public List<ReportResponseDTO> getSentReports(Long reporterId) {

        return reportRepo.findByReporterId(reporterId)
                .stream()
                .map(ReportMapper::toDTO)
                .toList();
    }

    // =========================
    // 3. USER HISTORY
    // =========================
    public List<ReportResponseDTO> getUserHistory(Long userId) {

        return reportRepo.findByReportedUserId(userId)
                .stream()
                .filter(r -> r.getStatus() == ReportStatus.RESOLVED)
                .map(ReportMapper::toDTO)
                .toList();
    }

    // =========================
    // 4. ADMIN: RESOLVE
    // =========================
    public ReportResponseDTO resolveReport(
            Long id,
            String solution,
            String action,
            String duration
    ) {

        Report report = reportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ReportStatus.RESOLVED);
        report.setResolutionDetails(solution);
        report.setActionTaken(action);
        report.setActionDuration(duration);
        report.setResolvedAt(LocalDateTime.now());

        return ReportMapper.toDTO(reportRepo.save(report));
    }

    // =========================
    // 5. ADMIN: GET ALL
    // =========================
    public List<ReportResponseDTO> getAllReports() {

        return reportRepo.findAllByOrderBySubmissionDateDesc()
                .stream()
                .map(ReportMapper::toDTO)
                .toList();
    }

    // =========================
    // 6. ADMIN: INVESTIGATE
    // =========================
    public ReportResponseDTO startInvestigation(Long id) {

        Report report = reportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ReportStatus.INVESTIGATING);

        return ReportMapper.toDTO(reportRepo.save(report));
    }

    // =========================
    // 7. ADMIN: DISMISS
    // =========================
    public ReportResponseDTO dismissReport(Long id, String reason) {

        Report report = reportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(ReportStatus.DISMISSED);
        report.setDismissalReason(reason);
        report.setResolvedAt(LocalDateTime.now());

        return ReportMapper.toDTO(reportRepo.save(report));
    }
}
