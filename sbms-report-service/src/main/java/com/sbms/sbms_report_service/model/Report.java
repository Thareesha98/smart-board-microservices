package com.sbms.sbms_report_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.sbms.sbms_report_service.common.BaseEntity;
import com.sbms.sbms_report_service.model.enums.ReportSeverity;
import com.sbms.sbms_report_service.model.enums.ReportStatus;
import com.sbms.sbms_report_service.model.enums.ReportType;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "report")
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "report_id"))
public class Report extends BaseEntity {


	@Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;     // boarding, owner, safety, fraud, other

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportSeverity severity; // low, medium, high, critical

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status; // pending, investigating, resolved, dismissed

    // --------------------
    // TIME
    // --------------------

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @Column(name = "incident_date")
    private LocalDate incidentDate;

    // --------------------
    // CONTEXT (ID ONLY)
    // --------------------

    /**
     * Who created the report (Student or Owner)
     */
    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    /**
     * Who is being reported (optional)
     */
    @Column(name = "reported_user_id")
    private Long reportedUserId;



    /**
     * Denormalized display field ONLY (optional)
     * Not authoritative
     */
    @Column(name = "boarding_id")
    private Long boardingId;

    @Column(name = "allow_contact")
    private boolean allowContact;

    // --------------------
    // ADMIN ACTION
    // --------------------

    @Column(name = "resolution_details", columnDefinition = "TEXT")
    private String resolutionDetails;

    @Column(name = "dismissal_reason", columnDefinition = "TEXT")
    private String dismissalReason;

    @Column(name = "action_taken")
    private String actionTaken;

    @Column(name = "action_duration")
    private String actionDuration;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @ElementCollection
    @CollectionTable(
        name = "report_evidence",
        joinColumns = @JoinColumn(name = "report_id")
    )
    @Column(name = "file_url")
    private List<String> evidence;


   

}
