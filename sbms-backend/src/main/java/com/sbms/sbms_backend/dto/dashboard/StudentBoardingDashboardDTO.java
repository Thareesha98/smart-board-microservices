package com.sbms.sbms_backend.dto.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sbms.sbms_backend.model.enums.RegistrationStatus;

@Data
public class StudentBoardingDashboardDTO {

    private Long registrationId;
    private RegistrationStatus status;
    private LocalDateTime registeredAt;

    private Long boardingId;
    private String boardingTitle;
    private String boardingAddress;
    private String ownerName;

    private BigDecimal keyMoney;
    private BigDecimal monthlyPrice;
    private BigDecimal currentMonthDue;
    private int dueInDays;
    private String paymentStatus;
    private LocalDate lastPaymentDate;

    private int openIssues;
    private int resolvedIssues;
    private LocalDate lastIssueDate;

    private Double averageRating;
    private boolean yourReviewSubmitted;
}
