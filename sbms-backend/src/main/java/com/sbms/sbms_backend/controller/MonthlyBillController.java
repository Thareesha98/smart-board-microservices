package com.sbms.sbms_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_backend.client.UserClient;
import com.sbms.sbms_backend.dto.billing.MonthlyBillResponseDTO;
import com.sbms.sbms_backend.dto.user.UserMinimalDTO;

import com.sbms.sbms_backend.service.MonthlyBillService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor // Using constructor injection instead of @Autowired
public class MonthlyBillController {

    private final MonthlyBillService billService;
    
    // FIX: Use UserClient instead of UserRepository
    private final UserClient userClient;
@GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public List<MonthlyBillResponseDTO> studentBills(Authentication authentication) {
        
        // Logic: Resolve student ID from User Service via email
        Long studentId = getUserIdFromAuth(authentication);

        return billService.getForStudent(studentId);
    }

       @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public List<MonthlyBillResponseDTO> ownerBills(Authentication authentication) {
        
        // Logic: Resolve owner ID from User Service via email
        Long ownerId = getUserIdFromAuth(authentication);

        return billService.getForOwner(ownerId);
    }
  @PostMapping("/generate/{month}")
    public ResponseEntity<String> generate(@PathVariable String month) {
        billService.generateBillsForMonth(month);
        return ResponseEntity.ok("Monthly bills generated for " + month);
    }

    /**
     * Helper method to resolve User ID from the JWT email
     */
    private Long getUserIdFromAuth(Authentication auth) {
        String email = auth.getName();
        
        // Reusing the findByEmail method we added to UserClient
        UserMinimalDTO user = userClient.findByEmail(email);
        
        if (user == null) {
            throw new RuntimeException("User not found in User Service");
        }
        return user.getId();
    }
}
