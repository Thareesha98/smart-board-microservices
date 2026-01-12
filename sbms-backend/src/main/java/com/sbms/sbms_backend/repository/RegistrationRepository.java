package com.sbms.sbms_backend.repository;

import com.sbms.sbms_backend.model.Registration;
import com.sbms.sbms_backend.model.User;
import com.sbms.sbms_backend.model.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // ---------------- STUDENT ----------------
    List<Registration> findByStudent(User student);

    List<Registration> findByStudentId(Long studentId);

    // ---------------- BOARDING ----------------
    List<Registration> findByBoardingId(Long boardingId);

    List<Registration> findByBoardingIdAndStatus(
            Long boardingId,
            RegistrationStatus status
    );

    // ---------------- OWNER (via Boarding Service) ----------------
    /**
     * Owner → get boardingIds from Boarding Service,
     * then query registrations by those IDs
     */
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

