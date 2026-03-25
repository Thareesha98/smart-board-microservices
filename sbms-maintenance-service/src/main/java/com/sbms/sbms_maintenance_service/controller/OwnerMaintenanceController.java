package com.sbms.sbms_maintenance_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_maintenance_service.dto.maintenance.MaintenanceDecisionDTO;
import com.sbms.sbms_maintenance_service.dto.maintenance.MaintenanceResponseDTO;
import com.sbms.sbms_maintenance_service.service.MaintenanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/maintenance/owner")
@RequiredArgsConstructor
public class OwnerMaintenanceController {

    private final MaintenanceService service;

    @GetMapping
    public List<MaintenanceResponseDTO> ownerList(
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        return service.getForOwner(ownerId);
    }

    @PutMapping("/{id}")
    public MaintenanceResponseDTO decide(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long ownerId,
            @RequestBody MaintenanceDecisionDTO dto
    ) {
        return service.decide(ownerId, id, dto);
    }
    
    
    
    @GetMapping("/technician")
    public List<MaintenanceResponseDTO> technicianJobs(
            @RequestHeader("X-User-Id") Long technicianId
    ) {
        return service.getForTechnician(technicianId);
    }
    
    
    
    
    @PutMapping("/{maintenanceId}/assign/{technicianId}")
    public String assignTechnician(
            @RequestHeader("X-User-Id") Long ownerId,
            @PathVariable Long maintenanceId,
            @PathVariable Long technicianId) {
        service.assignTechnician(ownerId, maintenanceId, technicianId);
        return "Assigned successfully.";
    }

    @PostMapping("/{maintenanceId}/review")
    public MaintenanceResponseDTO reviewTechnician(
            @RequestHeader("X-User-Id") Long ownerId,
            @PathVariable Long maintenanceId,
            @RequestParam int rating,
            @RequestParam String comment) {
        return service.reviewTechnician(ownerId, maintenanceId, rating, comment);
    }
}
