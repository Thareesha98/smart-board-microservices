package com.sbms.sbms_backend.mapper;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.sbms.sbms_backend.dto.dashboard.StudentBoardingDashboardDTO;
import com.sbms.sbms_backend.model.Registration;
import com.sbms.sbms_backend.record.BoardingSnapshot;

public class StudentBoardingDashboardMapper {

    public static StudentBoardingDashboardDTO toDTO(
            Registration reg,
            BoardingSnapshot boarding,
            BigDecimal currentMonthDue,
            String paymentStatus,
            LocalDate lastPaymentDate,
            int openIssues,
            int resolvedIssues,
            LocalDate lastIssueDate,
            Double avgRating,
            boolean reviewSubmitted
    ) {


        StudentBoardingDashboardDTO dto = new StudentBoardingDashboardDTO();

        // Registration
        dto.setRegistrationId(reg.getId());
        dto.setStatus(reg.getStatus());
        dto.setRegisteredAt(reg.getCreatedAt());

        // Boarding
        dto.setBoardingId(boarding.id());
        dto.setBoardingTitle(boarding.title());
        dto.setBoardingAddress(boarding.address());
        dto.setOwnerName(boarding.ownerName());

        // Payment
        dto.setKeyMoney(boarding.keyMoney());
        dto.setMonthlyPrice(boarding.pricePerMonth());
        dto.setCurrentMonthDue(currentMonthDue);
        dto.setPaymentStatus(paymentStatus);
        dto.setLastPaymentDate(lastPaymentDate);

        // Due calculation (mock: 30 days cycle)
        long daysSinceReg =
                ChronoUnit.DAYS.between(reg.getCreatedAt().toLocalDate(), LocalDate.now());
        dto.setDueInDays(Math.max(0, 30 - (int) daysSinceReg));

        // Maintenance
        dto.setOpenIssues(openIssues);
        dto.setResolvedIssues(resolvedIssues);
        dto.setLastIssueDate(lastIssueDate);

        // Reviews
        dto.setAverageRating(avgRating);
        dto.setYourReviewSubmitted(reviewSubmitted);

        return dto;
    }
}
