package com.sbms.sbms_registration_service.mapper;








import com.sbms.sbms_registration_service.dto.StudentBoardingDashboardDTO;
import com.sbms.sbms_registration_service.dto.external.BoardingSnapshot;
import com.sbms.sbms_registration_service.model.Registration;

import java.math.BigDecimal;
import java.time.LocalDate;


public class StudentBoardingDashboardMapper {

    private StudentBoardingDashboardMapper() {
        // utility class
    }

    public static StudentBoardingDashboardDTO toDTO(
            Registration registration,
            BoardingSnapshot boardingSnapshot,

            // ---- payment-service data ----
            BigDecimal currentMonthDue,
            int dueInDays,
            String paymentStatus,
            LocalDate lastPaymentDate,

            // ---- issue-service data ----
            int openIssues,
            int resolvedIssues,
            LocalDate lastIssueDate,

            // ---- review-service data ----
            Double averageRating,
            boolean yourReviewSubmitted,

            // ---- owner/user-service data ----
            String ownerName
    ) {

        StudentBoardingDashboardDTO dto = new StudentBoardingDashboardDTO();

        // -------------------------------
        // REGISTRATION (OWNED)
        // -------------------------------
        dto.setRegistrationId(registration.getId());
        dto.setStatus(registration.getStatus());
        dto.setRegisteredAt(registration.getCreatedAt());

        // -------------------------------
        // BOARDING (REFERENCE DATA)
        // -------------------------------
        dto.setBoardingId(boardingSnapshot.id());
        dto.setBoardingTitle(boardingSnapshot.title());
        dto.setBoardingAddress(null); // optional → fetch via boarding-service if needed
        dto.setOwnerName(ownerName);

        dto.setKeyMoney(boardingSnapshot.keyMoney());
        dto.setMonthlyPrice(boardingSnapshot.pricePerMonth());

        // -------------------------------
        // PAYMENT (EXTERNAL SERVICE)
        // -------------------------------
        dto.setCurrentMonthDue(
                currentMonthDue != null ? currentMonthDue : BigDecimal.ZERO
        );
        dto.setDueInDays(dueInDays);
        dto.setPaymentStatus(
                paymentStatus != null ? paymentStatus : "UNKNOWN"
        );
        dto.setLastPaymentDate(lastPaymentDate);

        // -------------------------------
        // MAINTENANCE / ISSUES (EXTERNAL)
        // -------------------------------
        dto.setOpenIssues(openIssues);
        dto.setResolvedIssues(resolvedIssues);
        dto.setLastIssueDate(lastIssueDate);

        // -------------------------------
        // REVIEWS (EXTERNAL)
        // -------------------------------
        dto.setAverageRating(averageRating);
        dto.setYourReviewSubmitted(yourReviewSubmitted);

        return dto;
    }
}

