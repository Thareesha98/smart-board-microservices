package com.sbms.sbms_backend.controller;

import com.sbms.sbms_backend.dto.appointment.*;
import com.sbms.sbms_backend.model.enums.AppointmentStatus;
import com.sbms.sbms_backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/*
@RestController
@RequestMapping("/api/appointments")
@CrossOrigin
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // ---------------------------------------------------------
    // STUDENT: CREATE APPOINTMENT REQUEST
    // POST /api/appointments/student/{studentId}
    // ---------------------------------------------------------
    @PostMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public AppointmentResponseDTO createAppointment(
            @PathVariable Long studentId,
            @RequestBody AppointmentCreateDTO dto
    ) {
        return appointmentService.createAppointment(studentId, dto);
    }

    // ---------------------------------------------------------
    // STUDENT: VIEW OWN APPOINTMENTS
    // GET /api/appointments/student/{studentId}
    // ---------------------------------------------------------
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public List<AppointmentResponseDTO> getStudentAppointments(
            @PathVariable Long studentId
    ) {
        return appointmentService.getAppointmentsForStudent(studentId);
    }

    // ---------------------------------------------------------
    // STUDENT: CANCEL APPOINTMENT
    // PUT /api/appointments/student/{studentId}/{appointmentId}/cancel
    // ---------------------------------------------------------
    @PutMapping("/student/{studentId}/{appointmentId}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public AppointmentResponseDTO cancelAppointment(
            @PathVariable Long studentId,
            @PathVariable Long appointmentId
    ) {
        return appointmentService.cancelAppointment(studentId, appointmentId);
    }

    // ---------------------------------------------------------
    // OWNER: VIEW APPOINTMENTS (OPTIONAL status filter)
    // GET /api/appointments/owner/{ownerId}?status=PENDING
    // ---------------------------------------------------------
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
    public List<AppointmentResponseDTO> getOwnerAppointments(
            @PathVariable Long ownerId,
            @RequestParam(required = false) AppointmentStatus status
    ) {
        return appointmentService.getAppointmentsForOwner(ownerId, status);
    }

    // ---------------------------------------------------------
    // OWNER: RESPOND (ACCEPT / DECLINE)
    // PUT /api/appointments/owner/{ownerId}/{appointmentId}
    // ---------------------------------------------------------
    @PutMapping("/owner/{ownerId}/{appointmentId}")
    @PreAuthorize("hasRole('OWNER')")
    public AppointmentResponseDTO respond(
            @PathVariable Long ownerId,
            @PathVariable Long appointmentId,
            @RequestBody AppointmentOwnerDecisionDTO dto
    ) {
        return appointmentService.respondToAppointment(ownerId, appointmentId, dto);
    }
}

*/



@RestController
@RequestMapping("/api/appointments")
@CrossOrigin
public class AppointmentController {

    private static final String MESSAGE =
            "Appointment service has been migrated. Please use appointment-service.";

    // ---------------------------------------------------------
    // ALL appointment endpoints are intentionally disabled
    // ---------------------------------------------------------
    
    @PostMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.GONE)
    public void deprecated() {
        throw new ResponseStatusException(
            HttpStatus.GONE,
            "Appointment service moved to appointment-service"
        );
    }


    @RequestMapping(
            value = {
                    "",
                    "/**"
            },
            method = {
                    RequestMethod.GET,
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.DELETE,
                    RequestMethod.PATCH
            }
    )
    public ResponseEntity<Map<String, Object>> appointmentsMigrated() {

        return ResponseEntity
                .status(HttpStatus.GONE)   // 🔥 410
                .body(Map.of(
                        "status", 410,
                        "error", "GONE",
                        "message", MESSAGE
                ));
    }
}


