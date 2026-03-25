package com.sbms.sbms_maintenance_service.controller;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_maintenance_service.dto.maintenance.MaintenanceResponseDTO;
import com.sbms.sbms_maintenance_service.service.MaintenanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/technician-workflow")
@RequiredArgsConstructor
public class TechnicianMaintenanceController {

    private final MaintenanceService service;

    // Technically you have a GET mapping for technician jobs in your Owner controller.
    // It's highly recommended to move it here, but I will leave it where it is to avoid breaking your current setup.
    
    @GetMapping
    public List<MaintenanceResponseDTO> getTechnicianJobs(@RequestHeader("X-User-Id") Long technicianId) {
        // Calls the service to fetch jobs assigned to this specific technician
        return service.getJobsByTechnicianId(technicianId);
    }
    
    
    
 // 5. Assign Technician (Match: PUT /{maintenanceId}/assign/{technicianId})
    @PutMapping("/{maintenanceId}/assign/{technicianId}")
    public String assignTechnician(
            @PathVariable Long maintenanceId, 
            @PathVariable Long technicianId, 
            @RequestHeader("X-User-Id") Long ownerId) {
        service.assignTechnician(ownerId, maintenanceId, technicianId);
        return "Assigned successfully.";
    }

    // 6. Review Technician (Match: POST /{maintenanceId}/review)
    @PostMapping("/{maintenanceId}/review")
    public ResponseEntity<MaintenanceResponseDTO> reviewTechnician(
            @PathVariable Long maintenanceId, 
            @RequestParam int rating, 
            @RequestParam String comment,
            @RequestHeader("X-User-Id") Long ownerId) {
        return ResponseEntity.ok(service.reviewTechnician(ownerId, maintenanceId, rating, comment));
    }

    @PutMapping("/{maintenanceId}/decision")
    public String technicianDecision(
            @RequestHeader("X-User-Id") Long technicianId,
            @PathVariable Long maintenanceId,
            @RequestParam boolean accept,
            @RequestParam(required = false) String reason) {
        
        if (!accept && (reason == null || reason.trim().isEmpty())) {
            throw new RuntimeException("Rejection reason is required.");
        }
        
        service.handleDecision(technicianId, maintenanceId, accept, reason);
        return accept ? "Accepted" : "Rejected";
    }

    @PutMapping("/{maintenanceId}/complete")
    public String markWorkDone(
            @RequestHeader("X-User-Id") Long technicianId,
            @PathVariable Long maintenanceId,
            @RequestParam BigDecimal amount) {
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("You must enter a valid final amount.");
        }

        service.markWorkDone(technicianId, maintenanceId, amount);
        return "Marked as done. Final bill set to: " + amount;
    }
}