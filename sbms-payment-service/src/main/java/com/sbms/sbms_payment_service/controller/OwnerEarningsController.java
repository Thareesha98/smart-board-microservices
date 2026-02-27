package com.sbms.sbms_payment_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_payment_service.client.UserClient;
import com.sbms.sbms_payment_service.dto.dashboard.OwnerEarningTransactionDTO;
import com.sbms.sbms_payment_service.dto.dashboard.OwnerEarningsSummaryDTO;
import com.sbms.sbms_payment_service.dto.user.UserMinimalDTO;
import com.sbms.sbms_payment_service.service.OwnerEarningsService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owner/earnings")
@RequiredArgsConstructor
public class OwnerEarningsController {

    private final OwnerEarningsService earningsService;
    private final UserClient userClient;

    // ===============================
    // 1️⃣ OWNER EARNINGS SUMMARY (FIXED - HEADER BASED)
    // ===============================
    @GetMapping("/summary")
    public OwnerEarningsSummaryDTO summary(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        // 🔒 Role validation (since no Spring Security JWT here)
        validateOwnerRole(role);

        Long ownerId = getOwnerIdFromEmail(email);
        return earningsService.getSummary(ownerId);
    }

    // ===============================
    // 2️⃣ OWNER RECENT TRANSACTIONS (FIXED - HEADER BASED)
    // ===============================
    @GetMapping("/transactions")
    public List<OwnerEarningTransactionDTO> recentTransactions(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        validateOwnerRole(role);

        Long ownerId = getOwnerIdFromEmail(email);
        return earningsService.recentTransactions(ownerId);
    }

    // ===============================
    // 🔒 HELPER: VALIDATE ROLE FROM GATEWAY HEADER
    // ===============================
    private void validateOwnerRole(String role) {
        if (role == null || !role.equalsIgnoreCase("OWNER")) {
            throw new RuntimeException("Forbidden: Owner access required");
        }
    }

    // ===============================
    // 🔍 HELPER: RESOLVE OWNER ID FROM USER SERVICE
    // ===============================
    private Long getOwnerIdFromEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Missing X-User-Email header from Gateway");
        }

        UserMinimalDTO user = userClient.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Owner not found in User Service");
        }

        return user.getId();
    }
}