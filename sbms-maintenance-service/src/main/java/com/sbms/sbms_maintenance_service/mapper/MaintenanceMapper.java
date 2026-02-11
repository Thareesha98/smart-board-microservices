package com.sbms.sbms_maintenance_service.mapper;

import java.util.Collections;
import java.util.List;

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

        dto.setStatus(m.getStatus());
        dto.setStudentNote(m.getStudentNote());
        dto.setOwnerNote(m.getOwnerNote());
        
        
        
        if (m.getImageUrls() != null) {
            dto.setImageUrls(List.copyOf(m.getImageUrls()));
        } else {
            dto.setImageUrls(Collections.emptyList());
        }


    

        return dto;
    }
}
