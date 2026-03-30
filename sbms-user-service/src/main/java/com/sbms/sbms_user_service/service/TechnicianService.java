package com.sbms.sbms_user_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sbms.sbms_user_service.dto.technical.TechnicianCardDTO;
import com.sbms.sbms_user_service.enums.MaintenanceIssueType;
import com.sbms.sbms_user_service.enums.UserRole;
import com.sbms.sbms_user_service.model.TechnicianReview;
import com.sbms.sbms_user_service.model.User;
import com.sbms.sbms_user_service.repository.TechnicianReviewRepository;
import com.sbms.sbms_user_service.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sbms.sbms_user_service.dto.technical.TechnicianCardDTO;
import com.sbms.sbms_user_service.dto.technical.TechnicianProfileResponseDTO;
import com.sbms.sbms_user_service.dto.technical.TechnicianReviewDTO;
import com.sbms.sbms_user_service.dto.user.TechnicianReviewRequest;
import com.sbms.sbms_user_service.enums.MaintenanceIssueType;
import com.sbms.sbms_user_service.enums.UserRole;
import com.sbms.sbms_user_service.model.TechnicianReview;
import com.sbms.sbms_user_service.model.User;
import com.sbms.sbms_user_service.repository.TechnicianReviewRepository;
import com.sbms.sbms_user_service.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TechnicianService {

    private final UserRepository userRepository;
    private final TechnicianReviewRepository reviewRepository;

    // --- MONOLITH REPLACEMENTS ---

    public TechnicianProfileResponseDTO getProfile(Long technicianId) {
        // 1. Recalculate stats before fetching profile (like the monolith did)
        updateTechnicianStats(technicianId);

        // 2. Fetch the updated user
        User tech = userRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        // 3. Map to DTO
        TechnicianProfileResponseDTO dto = new TechnicianProfileResponseDTO();
        dto.setId(tech.getId());
        dto.setFullName(tech.getFullName());
        dto.setEmail(tech.getEmail());
        dto.setPhone(tech.getPhone());
        dto.setProfileImageUrl(tech.getProfileImageUrl());
        dto.setNicNumber(tech.getNicNumber());
        dto.setDob(tech.getDob());
        dto.setGender(tech.getGender());
        dto.setAddress(tech.getAddress());
        dto.setCity(tech.getCity());
        dto.setProvince(tech.getProvince());
        dto.setAverageRating(tech.getTechnicianAverageRating());
        
        
        dto.setBasePrice(tech.getBasePrice() != null ? tech.getBasePrice() : 0.0);
        dto.setSkills(tech.getSkills());
        dto.setTotalJobsCompleted(tech.getTechnicianTotalJobs() != null ? tech.getTechnicianTotalJobs() : 0);
        
        

        return dto;
    }

    public List<TechnicianReviewDTO> getReviewsForTechnician(Long technicianId) {
        return reviewRepository.findByTechnicianId(technicianId).stream()
                .map(review -> {
                    TechnicianReviewDTO dto = new TechnicianReviewDTO();
                    dto.setId(review.getId());
                    
                    // Fetch owner details from User Repo
                    userRepository.findById(review.getOwnerId()).ifPresentOrElse(owner -> {
                        dto.setOwnerName(owner.getFullName());
                        dto.setOwnerProfileImageUrl(owner.getProfileImageUrl());
                    }, () -> {
                        dto.setOwnerName("Unknown Owner");
                        dto.setOwnerProfileImageUrl(null);
                    });

                    dto.setRating(review.getRating());
                    dto.setComment(review.getComment());
                    dto.setDate(review.getCreatedAt() != null ? review.getCreatedAt().toLocalDate() : LocalDate.now());
                    
                    return dto;
                }).toList();
    }

    public List<TechnicianCardDTO> findTechnicians(MaintenanceIssueType skill, String city) {
        return userRepository.findByRole(UserRole.TECHNICIAN).stream()
                .filter(t -> t.getSkills() != null && t.getSkills().contains(skill))
                .filter(t -> city == null || city.isEmpty() || (t.getCity() != null && t.getCity().equalsIgnoreCase(city)))
                .map(this::mapCard)
                .toList();
    }

    // --- HELPERS ---

    private TechnicianCardDTO mapCard(User user) {
        TechnicianCardDTO dto = new TechnicianCardDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setCity(user.getCity());
        dto.setBasePrice(user.getBasePrice());
        dto.setSkills(user.getSkills());
        dto.setAverageRating(user.getTechnicianAverageRating() != null ? user.getTechnicianAverageRating().doubleValue() : 0.0);
        dto.setTotalJobs(user.getTechnicianTotalJobs() != null ? user.getTechnicianTotalJobs() : 0);
        return dto;
    }

    @Transactional
    public void updateTechnicianStats(Long technicianId) {
        User tech = userRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        List<TechnicianReview> reviews = reviewRepository.findByTechnicianId(technicianId);
        
        // This simulates the completed jobs counting from the monolith
        // In a true microservice, you might want the Maintenance service to send an event to increment this
        tech.setTechnicianTotalJobs(tech.getTechnicianTotalJobs()); 

        if (!reviews.isEmpty()) {
            double avg = reviews.stream()
                    .mapToInt(TechnicianReview::getRating)
                    .average()
                    .orElse(0.0);
            tech.setTechnicianAverageRating(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
        }

        userRepository.save(tech);
    }
    
    
    
 // Add to TechnicianService.java in sbms-user-service

    @Transactional
    public void saveReviewAndRefreshStats(TechnicianReviewRequest req) {
        // 1. Save the review to the database
        TechnicianReview review = new TechnicianReview();
        review.setOwnerId(req.getOwnerId());
        review.setTechnicianId(req.getTechnicianId());
        review.setMaintenanceId(req.getMaintenanceId());
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        
        reviewRepository.save(review);

        // 2. Trigger the stats update logic you already wrote
        updateTechnicianStats(req.getTechnicianId());
        
        // 3. Logic for incrementing job count (since a review implies completion)
        User tech = userRepository.findById(req.getTechnicianId()).orElseThrow();
        tech.setTechnicianTotalJobs(tech.getTechnicianTotalJobs() + 1);
        userRepository.save(tech);
    }
    
 // Rename the method to match what the Controller is calling
    @Transactional
    public void addReviewFromMaintenance(TechnicianReviewRequest req) {
        // 1. Save the review to the database
        TechnicianReview review = new TechnicianReview();
        review.setOwnerId(req.getOwnerId());
        review.setTechnicianId(req.getTechnicianId());
        review.setMaintenanceId(req.getMaintenanceId());
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        
        reviewRepository.save(review);

        // 2. Trigger the stats update logic
        updateTechnicianStats(req.getTechnicianId());
        
        // 3. Increment job count
        User tech = userRepository.findById(req.getTechnicianId())
                .orElseThrow(() -> new RuntimeException("Technician not found"));
        
        // Ensure we handle null if it's a new tech
        int currentJobs = (tech.getTechnicianTotalJobs() != null) ? tech.getTechnicianTotalJobs() : 0;
        tech.setTechnicianTotalJobs(currentJobs + 1);
        
        userRepository.save(tech);
    }
    
    
    
    
}