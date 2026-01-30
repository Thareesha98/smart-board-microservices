package com.sbms.sbms_maintenance_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_maintenance_service.model.Maintenance;

public interface MaintenanceRepository
extends JpaRepository<Maintenance, Long> {

List<Maintenance> findByStudentId(Long studentId);

List<Maintenance> findByBoardingIdIn(List<Long> boardingIds);
}
