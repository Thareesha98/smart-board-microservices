package com.sbms.sbms_maintenance_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sbms.sbms_maintenance_service.client.BoardingClient;
import com.sbms.sbms_maintenance_service.client.UserClient;
import com.sbms.sbms_maintenance_service.dto.maintenance.*;
import com.sbms.sbms_maintenance_service.mapper.MaintenanceMapper;
import com.sbms.sbms_maintenance_service.model.Maintenance;
import com.sbms.sbms_maintenance_service.model.enums.MaintenanceStatus;
import com.sbms.sbms_maintenance_service.publisher.MaintenanceEventPublisher;
import com.sbms.sbms_maintenance_service.repository.MaintenanceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {

    private static final Logger log =
            LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceRepository repo;
    private final BoardingClient boardingClient;
    private final MaintenanceEventPublisher publisher;
    private final UserClient userClient;

    public MaintenanceResponseDTO create(
            Long studentId,
            MaintenanceCreateDTO dto
    ) {
        boardingClient.validateBoarding(dto.getBoardingId());

        Maintenance m = new Maintenance();
        m.setStudentId(studentId);
        m.setBoardingId(dto.getBoardingId());
        m.setTitle(dto.getTitle());
        m.setDescription(dto.getDescription());
        m.setStudentNote(dto.getStudentNote());
        m.setImageUrls(dto.getImageUrls());

        repo.save(m);

        //  SAFE EVENT PUBLISH
        try {
            publisher.created(m);
        } catch (Exception e) {
            log.warn("⚠️ Failed to publish maintenance.created", e);
        }

        return MaintenanceMapper.toDTO(m);
    }
    
    
    public List<MaintenanceResponseDTO> getJobsByTechnicianId(Long technicianId) {
        // 1. Fetch the raw maintenance records from the database
        List<Maintenance> jobs = repo.findByAssignedTechnicianId(technicianId);

        return jobs.stream()
            .map(m -> {
                // 2. Map basic fields (IDs, Status, Dates) using your static mapper
                MaintenanceResponseDTO dto = MaintenanceMapper.toDTO(m);

                // 3. ENRICH: Call Boarding Service for details using the ID from the job
                try {
                    BoardingFullSnapshot snapshot = boardingClient.getBoarding(m.getBoardingId());
                    if (snapshot != null) {
                        dto.setBoardingTitle(snapshot.title());
                        dto.setBoardingAddress(snapshot.address());
                        dto.setOwnerName("Mayura Pabasara");
                        // If you need the owner's phone, ensure it's added to the record/DTO
                    }
                } catch (Exception e) {
                    log.error("Could not fetch boarding details for ID: {}", m.getBoardingId(), e);
                    dto.setBoardingTitle("Boarding Info Unavailable");
                    dto.setOwnerName("Unknown Owner");
                }

                // 4. Manual Review Mapping (from your monolith logic)
                if (m.getOwnerRating() != null && m.getOwnerRating() > 0) {
                    dto.setOwnerRating(m.getOwnerRating());
                    dto.setOwnerComment(m.getOwnerComment());
                    dto.setReviewComment(m.getOwnerComment());
                }
                
                dto.setCreatedAt(m.getCreatedAt());
                dto.setUpdatedAt(m.getUpdatedAt());

                return dto;
            })
            .collect(Collectors.toList());
    }
    
    
    

    public List<MaintenanceResponseDTO> getForStudent(Long studentId) {
    	return repo.findByStudentId(studentId)
                .stream()
                .map(m -> {

                    MaintenanceResponseDTO dto = MaintenanceMapper.toDTO(m);

                    // 🔥 ENRICH BOARDING DATA
                    try {
                        BoardingFullSnapshot snapshot =
                                boardingClient.getBoarding(m.getBoardingId());

                        if (snapshot != null) {
                            dto.setBoardingTitle(snapshot.title());
                            dto.setBoardingAddress(snapshot.address());
                            dto.setOwnerName("Owner"); // or fetch properly
                        }

                    } catch (Exception e) {
                        log.warn("Boarding fetch failed", e);
                    }

                    return dto;
                })
                .toList();
    }

    public List<MaintenanceResponseDTO> getForOwner(Long ownerId) {
        List<Long> boardingIds =
                boardingClient.getBoardingIdsByOwner(ownerId);

        return repo.findByBoardingIdIn(boardingIds)
                .stream()
                .map(MaintenanceMapper::toDTO)
                .toList();
    }

    public MaintenanceResponseDTO decide(
            Long ownerId,
            Long id,
            MaintenanceDecisionDTO dto
    ) {
        Maintenance m = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        BoardingOwnerInfo ownerInfo =
                boardingClient.getBoardingOwner(m.getBoardingId());

        if (!ownerInfo.ownerId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }

        m.setStatus(dto.getStatus());
        m.setOwnerNote(dto.getOwnerNote());

        repo.save(m);

        // SAFE EVENT PUBLISH
        try {
            publisher.updated(m);
        } catch (Exception e) {
            log.warn("⚠️ Failed to publish maintenance.updated", e);
        }

        return MaintenanceMapper.toDTO(m);
    }
    
    
    
 

 public List<MaintenanceResponseDTO> getForTechnician(Long technicianId) {

     return repo.findByAssignedTechnicianId(technicianId)
             .stream()
             .map(MaintenanceMapper::toDTO)
             .toList();
 }
 
 
 
 
		 
		 
		//1. Assign Technician
		public void assignTechnician(Long maintenanceId, Long technicianId) {
		  Maintenance m = repo.findById(maintenanceId).orElseThrow();
		  m.setAssignedTechnicianId(technicianId);
		  m.setStatus(MaintenanceStatus.ASSIGNED);
		  repo.save(m);
		}
		
		//2. Technician Decision (Accept/Reject)
		public void handleDecision(Long id, boolean accepted, String reason) {
		  Maintenance m = repo.findById(id).orElseThrow();
		  if (accepted) {
		      m.setStatus(MaintenanceStatus.IN_PROGRESS);
		  } else {
		      m.setAssignedTechnicianId(null);
		      m.setStatus(MaintenanceStatus.PENDING);
		      m.setRejectedByTechnician(true);
		      m.setTechnicianRejectionReason(reason);
		  }
		  repo.save(m);
		}
		
		
		
		
		public void assignTechnician(Long ownerId, Long maintenanceId, Long technicianId) {
	        Maintenance m = repo.findById(maintenanceId)
	                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

	        // Verify Owner
	        BoardingOwnerInfo ownerInfo = boardingClient.getBoardingOwner(m.getBoardingId());
	        if (!ownerInfo.ownerId().equals(ownerId)) {
	            throw new RuntimeException("Unauthorized: You do not own this boarding.");
	        }

	        m.setAssignedTechnicianId(technicianId);
	        m.setStatus(MaintenanceStatus.ASSIGNED);
	        m.setRejectedByTechnician(false);
	        repo.save(m);
	        
	        try { publisher.updated(m); } catch (Exception e) { log.warn("Event failed", e); }
	    }

	    // 2. TECHNICIAN: Accept/Reject
	    public void handleDecision(Long technicianId, Long maintenanceId, boolean accepted, String reason) {
	        Maintenance m = repo.findById(maintenanceId)
	                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

	        if (!technicianId.equals(m.getAssignedTechnicianId())) {
	            throw new RuntimeException("Unauthorized: Job not assigned to you.");
	        }

	        if (accepted) {
	            m.setStatus(MaintenanceStatus.IN_PROGRESS);
	        } else {
	            m.setAssignedTechnicianId(null);
	            m.setStatus(MaintenanceStatus.PENDING);
	            m.setRejectedByTechnician(true);
	            m.setTechnicianRejectionReason(reason);
	        }
	        repo.save(m);
	        
	        try { publisher.updated(m); } catch (Exception e) { log.warn("Event failed", e); }
	    }

	    // 3. TECHNICIAN: Mark Work Done
	    public void markWorkDone(Long technicianId, Long maintenanceId, BigDecimal amount) {
	        Maintenance m = repo.findById(maintenanceId)
	                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

	        if (!technicianId.equals(m.getAssignedTechnicianId())) {
	            throw new RuntimeException("Unauthorized: Job not assigned to you.");
	        }

	        m.setStatus(MaintenanceStatus.WORK_DONE);
	        m.setTechnicianFee(amount); // Ensure technicianFee is in your Maintenance entity
	        repo.save(m);
	        
	        try { publisher.updated(m); } catch (Exception e) { log.warn("Event failed", e); }
	    }

	    // 4. OWNER: Review & Complete
	    @Transactional
	    public MaintenanceResponseDTO reviewTechnician(Long ownerId, Long maintenanceId, int rating, String comment) {
	        Maintenance m = repo.findById(maintenanceId)
	                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

	        // Verify Owner
	        BoardingOwnerInfo ownerInfo = boardingClient.getBoardingOwner(m.getBoardingId());
	        if (!ownerInfo.ownerId().equals(ownerId)) {
	            throw new RuntimeException("Unauthorized: You do not own this boarding.");
	        }

	        m.setOwnerRating(rating); // Ensure this is in your Maintenance entity
	        m.setOwnerComment(comment); // Ensure this is in your Maintenance entity
	        m.setStatus(MaintenanceStatus.COMPLETED);
	        
	        Maintenance saved = repo.save(m);

	        // Notify User Service via WebClient
	        if (m.getAssignedTechnicianId() != null) {
	            UserClient.TechnicianReviewRequest reviewReq = new UserClient.TechnicianReviewRequest();
	            reviewReq.setOwnerId(ownerId);
	            reviewReq.setTechnicianId(m.getAssignedTechnicianId());
	            reviewReq.setMaintenanceId(m.getId());
	            reviewReq.setRating(rating);
	            reviewReq.setComment(comment);
	            
	            try {
	                userClient.submitTechnicianReview(reviewReq); // userClient injected via constructor
	            } catch (Exception e) {
	                log.error("Failed to sync review with User Service", e);
	            }
	        }

	        try { publisher.updated(saved); } catch (Exception e) { log.warn("Event failed", e); }

	        return MaintenanceMapper.toDTO(saved);
	    }
}
