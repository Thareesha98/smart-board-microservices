package com.sbms.sbms_backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_backend.model.Maintenance;
import com.sbms.sbms_backend.model.User;

import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByStudent(User student);

    List<Maintenance> findByBoarding_Owner(User owner);
    
    int countByRegistration_IdAndStatus(Long regId, String status);

    Maintenance findTopByRegistration_IdOrderByCreatedAtDesc(Long regId);
}
