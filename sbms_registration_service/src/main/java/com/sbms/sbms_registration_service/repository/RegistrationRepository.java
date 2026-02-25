package com.sbms.sbms_registration_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sbms.sbms_registration_service.enums.RegistrationStatus;
import com.sbms.sbms_registration_service.model.Registration;

public interface RegistrationRepository
extends JpaRepository<Registration, Long> {
		
		List<Registration> findByStudentId(Long studentId);
		
		List<Registration> findByBoardingId(Long boardingId);
		
		@Query("""
		SELECT r FROM Registration r
		WHERE r.boardingId IN :boardingIds
		AND (:status IS NULL OR r.status = :status)
		""")
		List<Registration> findByBoardingIdsAndStatus(
		    List<Long> boardingIds,
		    RegistrationStatus status
		);
}
