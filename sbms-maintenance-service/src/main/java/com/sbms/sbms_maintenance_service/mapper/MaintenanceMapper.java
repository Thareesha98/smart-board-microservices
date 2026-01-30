package com.sbms.sbms_maintenance_service.mapper;

import com.sbms.sbms_maintenance_service.dto.maintenance.MaintenanceResponseDTO;
import com.sbms.sbms_maintenance_service.model.Maintenance;

public class MaintenanceMapper {

    public static MaintenanceResponseDTO toDTO(
    		Maintenance m) {

        MaintenanceResponseDTO dto = new MaintenanceResponseDTO();

        dto.setId(m.getId());
        dto.setBoardingId(m.getBoardingId());

        
        dto.setStudentId(m.getStudentId());

        dto.setTitle(m.getTitle());
        dto.setDescription(m.getDescription());
        dto.setImageUrls(m.getImageUrls());

        dto.setStatus(m.getStatus());
        dto.setStudentNote(m.getStudentNote());
        dto.setOwnerNote(m.getOwnerNote());

        return dto;
    }
}
