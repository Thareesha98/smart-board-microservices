package com.sbms.appointment_service.service;

import com.sbms.appointment_service.client.UserClient;
import com.sbms.appointment_service.domain.Appointment;
import com.sbms.appointment_service.domain.AppointmentStatus;
import com.sbms.appointment_service.dto.*;
import com.sbms.appointment_service.event.AppointmentEventPublisher;
import com.sbms.appointment_service.mapper.AppointmentMapper;
import com.sbms.appointment_service.repository.AppointmentIdempotencyRepository;
import com.sbms.appointment_service.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentEventPublisher eventPublisher;
    private final BoardingResilienceService boardingResilienceService;
    private final  UserClient userClient;
    
    
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            AppointmentIdempotencyRepository idempotencyRepository,
            AppointmentEventPublisher eventPublisher,
            BoardingResilienceService boardingResilienceService,
            UserClient userClient
    ) {
        this.appointmentRepository = appointmentRepository;
        this.eventPublisher = eventPublisher;
        this.boardingResilienceService = boardingResilienceService;
        this.userClient = userClient;
    }

 
    public AppointmentResponseDTO createAppointment(
            Long studentId,
            AppointmentCreateDTO dto
    ) {
        BoardingOwnerInfo ownerInfo =
                boardingResilienceService.getBoardingOwner(dto.getBoardingId());

        Appointment appointment = new Appointment();
        appointment.setStudentId(studentId);
        appointment.setOwnerId(ownerInfo.ownerId());
        appointment.setBoardingId(dto.getBoardingId());
        appointment.setNumberOfStudents(dto.getNumberOfStudents());
        appointment.setRequestedStartTime(dto.getRequestedStartTime());
        appointment.setRequestedEndTime(dto.getRequestedEndTime());
        appointment.setStudentNote(dto.getStudentNote());
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment saved = appointmentRepository.save(appointment);

        // 🔔 Notify OWNER
        eventPublisher.publish(
                "appointment.created",
                ownerInfo.ownerId(),
                saved.getId(),
                Map.of(
                        "appointmentId", saved.getId(),
                        "studentId", studentId,
                        "boardingId", dto.getBoardingId(),
                        "boardingTitle", ownerInfo.boardingTitle()
                )
        );

        return AppointmentMapper.toDto(saved);
    }

    
    
    public List<AppointmentResponseDTO> getAppointmentsForStudent(Long studentId) {

        List<Appointment> appointments =
                appointmentRepository.findByStudentId(studentId);

        Map<Long, String> titles = resolveBoardingTitles(appointments);

        return appointments.stream()
                .map(a -> enrichAppointment(a, titles.get(a.getBoardingId())))
                .collect(Collectors.toList());
    }

    
    public List<AppointmentResponseDTO> getAppointmentsForOwner(
            Long ownerId,
            AppointmentStatus status
    ) {

        List<Appointment> appointments = (status == null)
                ? appointmentRepository.findByOwnerId(ownerId)
                : appointmentRepository.findByOwnerIdAndStatus(ownerId, status);

        Map<Long, String> titles = resolveBoardingTitles(appointments);

        return appointments.stream()
                .map(a -> enrichAppointment(a, titles.get(a.getBoardingId())))
                .collect(Collectors.toList());
    }
    
    
    private Map<Long, String> resolveBoardingTitles(List<Appointment> appointments) {
        return appointments.stream()
                .map(Appointment::getBoardingId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> boardingResilienceService
                                .getBoardingOwner(id)
                                .boardingTitle()
                ));
    }



    public AppointmentResponseDTO respondToAppointment(
            Long ownerId,
            Long appointmentId,
            AppointmentOwnerDecisionDTO dto
    ) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }

       

        if (dto.getStatus() == AppointmentStatus.DECLINED) {

            appointment.setStatus(AppointmentStatus.DECLINED);
            appointment.setOwnerNote(dto.getOwnerNote());

            eventPublisher.publish(
                    "appointment.declined",
                    appointment.getStudentId(),
                    appointment.getId(),
                    Map.of(
                            "appointmentId", appointment.getId(),
                            "boardingId", appointment.getBoardingId(),
                            "reason", dto.getOwnerNote()
                    )
            );

        } else if (dto.getStatus() == AppointmentStatus.ACCEPTED) {

            if (dto.getOwnerStartTime() == null || dto.getOwnerEndTime() == null) {
                throw new RuntimeException("Owner time slot is required");
            }

            appointment.setOwnerStartTime(dto.getOwnerStartTime());
            appointment.setOwnerEndTime(dto.getOwnerEndTime());
            appointment.setOwnerNote(dto.getOwnerNote());
            appointment.setStatus(AppointmentStatus.ACCEPTED);

            eventPublisher.publish(
                    "appointment.accepted",
                    appointment.getStudentId(),
                    appointment.getId(),
                    Map.of(
                            "appointmentId", appointment.getId(),
                            "boardingId", appointment.getBoardingId(),
                            "ownerStartTime", dto.getOwnerStartTime(),
                            "ownerEndTime", dto.getOwnerEndTime()
                    )
            );
        }

        return AppointmentMapper.toDto(appointmentRepository.save(appointment));
    }


    public AppointmentResponseDTO cancelAppointment(
            Long studentId,
            Long appointmentId
    ) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getStudentId().equals(studentId)) {
            throw new RuntimeException("Unauthorized");
        }


        appointment.setStatus(AppointmentStatus.CANCELLED);

        eventPublisher.publish(
                "appointment.cancelled",
                appointment.getOwnerId(),
                appointment.getId(),
                Map.of(
                        "appointmentId", appointment.getId(),
                        "studentId", appointment.getStudentId(),
                        "boardingId", appointment.getBoardingId()
                )
        );

        return AppointmentMapper.toDto(appointmentRepository.save(appointment));
    }
    

 public AppointmentResponseDTO markAsVisited(
         Long studentId,
         Long appointmentId
 ) {

     Appointment appointment = appointmentRepository.findById(appointmentId)
             .orElseThrow(() -> new RuntimeException("Appointment not found"));

     if (!appointment.getStudentId().equals(studentId)) {
         throw new RuntimeException("Unauthorized");
     }

     if (appointment.getStatus() != AppointmentStatus.ACCEPTED) {
         throw new RuntimeException("Only ACCEPTED appointments can be marked as visited");
     }

     appointment.setStatus(AppointmentStatus.VISITED);

     return AppointmentMapper.toDto(appointmentRepository.save(appointment));
 }

 
 public AppointmentResponseDTO selectBoarding(
         Long studentId,
         Long appointmentId
 ) {

     Appointment appointment = appointmentRepository.findById(appointmentId)
             .orElseThrow(() -> new RuntimeException("Appointment not found"));

     if (!appointment.getStudentId().equals(studentId)) {
         throw new RuntimeException("Unauthorized");
     }

     if (appointment.getStatus() != AppointmentStatus.VISITED) {
         throw new RuntimeException("You must visit before selecting");
     }

     appointment.setStatus(AppointmentStatus.SELECTED);

     return AppointmentMapper.toDto(appointmentRepository.save(appointment));
 }

 
 public AppointmentResponseDTO rejectBoarding(
         Long studentId,
         Long appointmentId
 ) {

     Appointment appointment = appointmentRepository.findById(appointmentId)
             .orElseThrow(() -> new RuntimeException("Appointment not found"));

     if (!appointment.getStudentId().equals(studentId)) {
         throw new RuntimeException("Unauthorized");
     }

     if (appointment.getStatus() != AppointmentStatus.VISITED) {
         throw new RuntimeException("Only visited appointments can be rejected");
     }

     appointment.setStatus(AppointmentStatus.NOT_SELECTED);

     return AppointmentMapper.toDto(appointmentRepository.save(appointment));
 }

 public List<AppointmentResponseDTO> getRecentAppointments(Long ownerId) {

	    List<Appointment> appointments =
	            appointmentRepository.findTop5ByOwnerIdOrderByIdDesc(ownerId);

	    Map<Long, String> titles = resolveBoardingTitles(appointments);

	    return appointments.stream()
	            .map(a -> enrichAppointment(a, titles.get(a.getBoardingId())))
	            .collect(Collectors.toList());
	}
 
 private AppointmentResponseDTO enrichAppointment(Appointment appointment, String boardingTitle) {

	    AppointmentResponseDTO dto = AppointmentMapper.toDto(appointment, boardingTitle);

	    try {

	        UserSnapshotDTO student = userClient.getUserSnapshot(appointment.getStudentId());

	        if (student != null) {
	            dto.setStudentName(student.getFullName());
	            dto.setStudentEmail(student.getEmail());
	        }

	        UserSnapshotDTO owner = userClient.getUserSnapshot(appointment.getOwnerId());

	        if (owner != null) {
	            dto.setOwnerName(owner.getFullName());
	            dto.setOwnerContact(owner.getPhone());
	        }

	    } catch (Exception e) {
	        System.out.println("User service unavailable: " + e.getMessage());
	    }

	    return dto;
	}
 
    
    
}
