package com.sbms.appointment_service.repository;


import com.sbms.appointment_service.domain.AppointmentIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentIdempotencyRepository
        extends JpaRepository<AppointmentIdempotency, Long> {

    Optional<AppointmentIdempotency> findByIdempotencyKey(String key);
}
