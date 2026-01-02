package com.sbms.sbms_backend.repository;

import com.sbms.sbms_backend.model.Appointment;
import com.sbms.sbms_backend.model.User;
import com.sbms.sbms_backend.model.Boarding;
import com.sbms.sbms_backend.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStudent(User student);

    List<Appointment> findByBoarding(Boarding boarding);

    List<Appointment> findByBoarding_Owner(User owner);

    List<Appointment> findByBoarding_OwnerAndStatus(User owner, AppointmentStatus status);
}
