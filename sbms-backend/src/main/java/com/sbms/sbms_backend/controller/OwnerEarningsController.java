package com.sbms.sbms_backend.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_backend.client.UserClient;
import com.sbms.sbms_backend.dto.dashboard.OwnerEarningTransactionDTO;
import com.sbms.sbms_backend.dto.dashboard.OwnerEarningsSummaryDTO;
import com.sbms.sbms_backend.dto.user.UserMinimalDTO;
import com.sbms.sbms_backend.service.OwnerEarningsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owner/earnings")
@RequiredArgsConstructor
public class OwnerEarningsController {

    private final OwnerEarningsService earningsService;
    
    // FIX: Use UserClient instead of UserRepository
    private final UserClient userClient; 

    @GetMapping("/summary")
    @PreAuthorize("hasRole('OWNER')")
    public OwnerEarningsSummaryDTO summary(Authentication auth) {
        // Logic: Get ownerId from User Service via Client
        Long ownerId = getOwnerIdFromAuth(auth);
        return earningsService.getSummary(ownerId);
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('OWNER')")
    public List<OwnerEarningTransactionDTO> recentTransactions(Authentication auth) {
        Long ownerId = getOwnerIdFromAuth(auth);
        return earningsService.recentTransactions(ownerId);
    }

    /**
     * Helper to resolve ownerId from the authenticated email
     */
    private Long getOwnerIdFromAuth(Authentication auth) {
        String email = auth.getName();
        
        // You should add a method to your UserClient to find user by email
        UserMinimalDTO user = userClient.findByEmail(email); 
        
        if (user == null) {
            throw new RuntimeException("Owner not found in User Service");
        }
        return user.getId();
    }
}

