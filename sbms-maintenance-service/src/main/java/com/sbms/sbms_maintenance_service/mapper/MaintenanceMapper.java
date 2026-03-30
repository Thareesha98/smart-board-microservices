package com.sbms.sbms_maintenance_service.mapper;

import java.util.Collections;
import java.util.List;

import com.sbms.sbms_maintenance_service.dto.maintenance.MaintenanceResponseDTO;
import com.sbms.sbms_maintenance_service.model.Maintenance;
//
//public class MaintenanceMapper {
//
//    public static MaintenanceResponseDTO toDTO(
//    		Maintenance m) {
//
//        MaintenanceResponseDTO dto = new MaintenanceResponseDTO();
//
//        dto.setId(m.getId());
//        dto.setBoardingId(m.getBoardingId());
//
//        
//        dto.setStudentId(m.getStudentId());
//
//        dto.setTitle(m.getTitle());
//        dto.setDescription(m.getDescription());
//
//        dto.setStatus(m.getStatus());
//        dto.setStudentNote(m.getStudentNote());
//        dto.setOwnerNote(m.getOwnerNote());
//        
//        
//        
//        if (m.getImageUrls() != null) {
//            dto.setImageUrls(List.copyOf(m.getImageUrls()));
//        } else {
//            dto.setImageUrls(Collections.emptyList());
//        }
//
//
//    
//
//        return dto;
//    }
//}





public class MaintenanceMapper {

    public static MaintenanceResponseDTO toDTO(Maintenance m) {

        MaintenanceResponseDTO dto = new MaintenanceResponseDTO();

        // --- EXISTING MAPPINGS (UNTOUCHED) ---
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

        // --- NEW TECHNICIAN & MICROSERVICE MAPPINGS ADDED ---
        dto.setRegistrationId(m.getRegistrationId());
        dto.setMaintnanceIssueType(m.getMaintenanceIssueType());
        dto.setMaintenanceUrgency(m.getMaintenanceUrgency());
        
        dto.setAssignedTechnicianId(m.getAssignedTechnicianId());
        dto.setRejectedByTechnician(m.isRejectedByTechnician());
        dto.setTechnicianRejectionReason(m.getTechnicianRejectionReason());
        dto.setTechnicianFee(m.getTechnicianFee());
        
     
        dto.setCreatedAt(m.getCreatedAt());
        dto.setUpdatedAt(m.getUpdatedAt());
        
        
        
        dto.setOwnerRating(m.getOwnerRating());
        dto.setOwnerComment(m.getOwnerComment());
        dto.setOwnerRating(m.getOwnerRating());
        dto.setOwnerComment(m.getOwnerComment());

        return dto;
    }
}
