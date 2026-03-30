package com.sbms.sbms_user_service.controller;

import com.sbms.sbms_user_service.dto.technical.*;
import com.sbms.sbms_user_service.dto.user.TechnicianReviewRequest;
import com.sbms.sbms_user_service.enums.MaintenanceIssueType;
import com.sbms.sbms_user_service.service.TechnicianService;
import com.sbms.sbms_user_service.client.MaintenanceClient;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/technician-workflow") 
@RequiredArgsConstructor
public class TechnicianController {

    private final TechnicianService service;
    private final MaintenanceClient maintenanceClient; 

    @GetMapping("/profile")
    public TechnicianProfileResponseDTO getMyProfile(@RequestHeader("X-User-Id") Long technicianId) {
        return service.getProfile(technicianId);
    }
    
 

    @GetMapping("/search")
    public List<TechnicianCardDTO> findTechnicians(
            @RequestParam MaintenanceIssueType skill,
            @RequestParam(required = false) String city) {
        return service.findTechnicians(skill, city);
    }

    @GetMapping("/reviews")
    public List<TechnicianReviewDTO> getMyReviews(@RequestHeader("X-User-Id") Long technicianId) {
        return service.getReviewsForTechnician(technicianId);
    }

    @GetMapping("/my-jobs")
    public List<MaintenanceResponseDTO> getMyJobs(@RequestHeader("X-User-Id") Long technicianId) {
        return maintenanceClient.getJobs(technicianId);
    }

    @PostMapping("/reviews/internal")
    public void submitReview(@RequestBody TechnicianReviewRequest req) {
        service.addReviewFromMaintenance(req); 
    }
}