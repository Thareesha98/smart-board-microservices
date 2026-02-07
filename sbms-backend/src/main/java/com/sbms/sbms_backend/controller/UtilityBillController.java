package com.sbms.sbms_backend.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_backend.client.UserClient;
import com.sbms.sbms_backend.dto.billing.CreateUtilityBillDTO;
import com.sbms.sbms_backend.dto.billing.UtilityBillResponseDTO;
import com.sbms.sbms_backend.dto.user.UserMinimalDTO;
import com.sbms.sbms_backend.service.UtilityBillService;

import lombok.RequiredArgsConstructor;




@RestController
@RequestMapping("/api/owner/utilities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class UtilityBillController {

    private final UtilityBillService utilityService;
    
    // FIX: Use UserClient instead of UserRepository
    private final UserClient userClient;

    @GetMapping
    public List<UtilityBillResponseDTO> myUtilities(Authentication auth) {
        String email = auth.getName();

        // Resolve ID from User Service
        UserMinimalDTO user = userClient.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Owner not found");
        }

        return utilityService.getForOwner(user.getId());
    }

    @PostMapping
    public void save(@RequestBody CreateUtilityBillDTO dto) {
        utilityService.createOrUpdate(dto);
    }

    /**
     * Helper to resolve ownerId from the authenticated email
     */
    private Long getOwnerIdFromAuth(Authentication auth) {
        String email = auth.getName();
        UserMinimalDTO user = userClient.findByEmail(email);
        
        if (user == null) {
            throw new RuntimeException("Owner profile not found in User Service");
        }
        return user.getId();
    }
}