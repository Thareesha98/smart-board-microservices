package com.sbms.sbms_backend.repository;

import com.sbms.sbms_backend.model.Registration;
import com.sbms.sbms_backend.model.enums.RegistrationStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
	List<Registration> findByStudentId(Long studentId);

    // ---------------------------------------------------------
    // BOARDING
    // ---------------------------------------------------------
    List<Registration> findByBoardingId(Long boardingId);

    List<Registration> findByBoardingIdAndStatus(
            Long boardingId,
            RegistrationStatus status
    );

    List<Registration> findByBoardingIdInAndStatus(
            List<Long> boardingIds,
            RegistrationStatus status
    );

    // ---------------------------------------------------------
    // DUPLICATE / BUSINESS CHECKS
    // ---------------------------------------------------------
    @Query("""
        SELECT COUNT(r) > 0
        FROM Registration r
        WHERE r.studentId = :studentId
          AND r.boardingId = :boardingId
          AND r.status IN (
                com.sbms.sbms_backend.model.enums.RegistrationStatus.PENDING,
                com.sbms.sbms_backend.model.enums.RegistrationStatus.APPROVED,
                com.sbms.sbms_backend.model.enums.RegistrationStatus.LEAVE_REQUESTED
          )
    """)
    boolean existsActiveRegistration(
            @Param("studentId") Long studentId,
            @Param("boardingId") Long boardingId
    );

    boolean existsByStudentIdAndBoardingIdAndStatus(
            Long studentId,
            Long boardingId,
            RegistrationStatus status
    );
    
    
    
    
 // NEW: Needed for UtilityBill mapping to calculate per-student costs
    long countByBoardingIdAndStatus(Long boardingId, RegistrationStatus status);

    // NEW: Needed for Owner view to see all registrations across their properties
    List<Registration> findByBoardingIdIn(List<Long> boardingIds);
}

