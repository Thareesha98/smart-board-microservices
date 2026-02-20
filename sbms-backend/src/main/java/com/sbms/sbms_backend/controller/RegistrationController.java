package com.sbms.sbms_backend.controller;

import com.sbms.sbms_backend.client.UserClient;
import com.sbms.sbms_backend.dto.dashboard.StudentBoardingDashboardDTO;
import com.sbms.sbms_backend.dto.registration.*;
import com.sbms.sbms_backend.dto.user.UserMinimalDTO;
import com.sbms.sbms_backend.model.enums.RegistrationStatus;
import com.sbms.sbms_backend.service.RegistrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private UserClient userClient;

    @PostMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public RegistrationResponseDTO register(
            @PathVariable Long studentId,
            @RequestBody RegistrationRequestDTO dto
    ) {
        return registrationService.register(studentId, dto);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public List<RegistrationResponseDTO> studentRegistrations(
            @PathVariable Long studentId
    ) {
        return registrationService.getStudentRegistrations(studentId);
    }

    @PutMapping("/student/{studentId}/{regId}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public RegistrationResponseDTO cancel(
            @PathVariable Long studentId,
            @PathVariable Long regId
    ) {
        return registrationService.cancel(studentId, regId);
    }

    // ================= OWNER =================

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
    public List<RegistrationResponseDTO> ownerRegistrations(
            @PathVariable Long ownerId,
            @RequestParam(required = false) RegistrationStatus status
    ) {
        return registrationService.getOwnerRegistrations(ownerId, status);
    }

    @PutMapping("/owner/{ownerId}/{regId}")
    @PreAuthorize("hasRole('OWNER')")
    public RegistrationResponseDTO decide(
            @PathVariable Long ownerId,
            @PathVariable Long regId,
            @RequestBody RegistrationDecisionDTO dto
    ) {
        return registrationService.decide(ownerId, regId, dto);
    }


    @GetMapping("/{regId}/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentBoardingDashboardDTO> dashboard(
            @PathVariable Long regId,
            Authentication authentication
    ) {
        // 1. Get user email from the JWT (provided by Spring Security)
        String email = authentication.getName();

        // 2. Resolve Student ID from the User Service via Client
        UserMinimalDTO user = userClient.findByEmail(email);
        
        if (user == null) {
            throw new RuntimeException("Student profile not found in User Service");
        }

        // 3. Fetch dashboard data using the verified student ID
        StudentBoardingDashboardDTO dto =
                registrationService.getDashboard(regId, user.getId());

        return ResponseEntity.ok(dto);
    }


    
    
    
   
}
