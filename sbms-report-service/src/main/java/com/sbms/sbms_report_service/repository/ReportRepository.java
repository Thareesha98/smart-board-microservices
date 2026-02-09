package com.sbms.sbms_report_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sbms.sbms_report_service.model.Report;
import com.sbms.sbms_report_service.model.enums.ReportStatus;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByReporterId(Long reporterId);

    List<Report> findByReportedUserId(Long userId);

    List<Report> findAllByOrderBySubmissionDateDesc();
    
    long countByStatus(ReportStatus status);
    
    List<Report> findByStatus(ReportStatus status);
}
