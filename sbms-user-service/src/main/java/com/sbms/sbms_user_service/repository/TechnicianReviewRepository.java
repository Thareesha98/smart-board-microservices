package com.sbms.sbms_user_service.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_user_service.model.TechnicianReview;

public interface TechnicianReviewRepository
        extends JpaRepository<TechnicianReview, Long> {

    List<TechnicianReview> findByTechnicianId(Long technicianId);

}