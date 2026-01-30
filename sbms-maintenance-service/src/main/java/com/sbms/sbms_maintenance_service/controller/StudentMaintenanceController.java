package com.sbms.sbms_maintenance_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_maintenance_service.dto.maintenance.MaintenanceCreateDTO;
import com.sbms.sbms_maintenance_service.dto.maintenance.MaintenanceResponseDTO;
import com.sbms.sbms_maintenance_service.service.MaintenanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class StudentMaintenanceController {

    private final MaintenanceService service;

    @PostMapping
    public MaintenanceResponseDTO create(
            @RequestHeader("X-User-Id") Long studentId,
            @RequestBody MaintenanceCreateDTO dto
    ) {
        return service.create(studentId, dto);
    }

    @GetMapping("/student")
    public List<MaintenanceResponseDTO> my(
            @RequestHeader("X-User-Id") Long studentId
    ) {
        return service.getForStudent(studentId);
    }
}
