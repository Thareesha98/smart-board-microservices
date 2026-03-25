package com.sbms.appointment_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.appointment_service.domain.Appointment;
import com.sbms.appointment_service.domain.AppointmentStatus;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

   

    List<Appointment> findByStudentId(Long studentId);

    List<Appointment> findByStudentIdAndStatus(Long studentId, AppointmentStatus status);

   

    List<Appointment> findByOwnerId(Long ownerId);

    List<Appointment> findByOwnerIdAndStatus(Long ownerId, AppointmentStatus status);

   

    List<Appointment> findByBoardingId(Long boardingId);
    
    List<Appointment> findTop5ByOwnerIdOrderByIdDesc(Long ownerId);
}
