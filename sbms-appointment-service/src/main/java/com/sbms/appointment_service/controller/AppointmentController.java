package com.sbms.appointment_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import com.sbms.appointment_service.domain.AppointmentStatus;
import com.sbms.appointment_service.dto.AppointmentCreateDTO;
import com.sbms.appointment_service.dto.AppointmentOwnerDecisionDTO;
import com.sbms.appointment_service.dto.AppointmentResponseDTO;
import com.sbms.appointment_service.service.AppointmentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    
    @PostMapping("/student/{studentId}")
    public AppointmentResponseDTO createAppointment(
            @PathVariable Long studentId,
            @RequestBody AppointmentCreateDTO dto,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role
    ) {

        if (!"STUDENT".equals(role)) {
            throw new RuntimeException("User is not a student");
        }

        if (!studentId.equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        return appointmentService.createAppointment(studentId, dto);
    }

   
    @GetMapping("/student/{studentId}")
    public List<AppointmentResponseDTO> getStudentAppointments(
            @PathVariable Long studentId,
            HttpServletRequest request
    ) {
        validateRole(request, "STUDENT");
        validateUser(request, studentId);

        return appointmentService.getAppointmentsForStudent(studentId);
    }

   
    @PutMapping("/student/{studentId}/{appointmentId}/cancel")
    public AppointmentResponseDTO cancelAppointment(
            @PathVariable Long studentId,
            @PathVariable Long appointmentId,
            @RequestBody(required = false) Map<String, String> payload,
            HttpServletRequest request
    ) {
        validateRole(request, "STUDENT");
        validateUser(request, studentId);

        String reason = payload != null ? payload.get("reason") : null;

        return appointmentService.cancelAppointment(studentId, appointmentId);
    }

    @PutMapping("/student/{studentId}/{appointmentId}/visit")
    public AppointmentResponseDTO markVisited(
            @PathVariable Long studentId,
            @PathVariable Long appointmentId,
            HttpServletRequest request
    ) {
        validateRole(request, "STUDENT");
        validateUser(request, studentId);

        return appointmentService.markAsVisited(studentId, appointmentId);
    }

    @PutMapping("/student/{studentId}/{appointmentId}/select")
    public AppointmentResponseDTO selectBoarding(
            @PathVariable Long studentId,
            @PathVariable Long appointmentId,
            HttpServletRequest request
    ) {
        validateRole(request, "STUDENT");
        validateUser(request, studentId);

        return appointmentService.selectBoarding(studentId, appointmentId);
    }

   
    @PutMapping("/student/{studentId}/{appointmentId}/reject")
    public AppointmentResponseDTO rejectBoarding(
            @PathVariable Long studentId,
            @PathVariable Long appointmentId,
            HttpServletRequest request
    ) {
        validateRole(request, "STUDENT");
        validateUser(request, studentId);

        return appointmentService.rejectBoarding(studentId, appointmentId);
    }

    
    //Ressilience4jjjjjjj
    
    @GetMapping("/owner/{ownerId}")
    public List<AppointmentResponseDTO> getOwnerAppointments(
            @PathVariable Long ownerId,
            @RequestParam(required = false) AppointmentStatus status,
            HttpServletRequest request
    ) {
        validateRole(request, "OWNER");
        validateUser(request, ownerId);

        return appointmentService.getAppointmentsForOwner(ownerId, status);
    }

    
    @PutMapping("/owner/{ownerId}/{appointmentId}")
    public AppointmentResponseDTO respond(
            @PathVariable Long ownerId,
            @PathVariable Long appointmentId,
            @RequestBody AppointmentOwnerDecisionDTO dto,
            HttpServletRequest request
    ) {
        validateRole(request, "OWNER");
        validateUser(request, ownerId);

        return appointmentService.respondToAppointment(ownerId, appointmentId, dto);
    }

    
    @GetMapping("/owner/{ownerId}/recent")
    public List<AppointmentResponseDTO> getRecentAppointments(
            @PathVariable Long ownerId,
            HttpServletRequest request
    ) {
        validateRole(request, "OWNER");
        validateUser(request, ownerId);

        return appointmentService.getRecentAppointments(ownerId);
    }

    
    private void validateRole(HttpServletRequest request, String expectedRole) {
        String role = request.getHeader("X-User-Role");
        if (!expectedRole.equals(role)) {
            throw new RuntimeException("Forbidden");
        }
    }

    private void validateUser(HttpServletRequest request, Long pathUserId) {
        Long headerUserId = Long.valueOf(request.getHeader("X-User-Id"));
        if (!headerUserId.equals(pathUserId)) {
            throw new RuntimeException("Unauthorized");
        }
    }
}