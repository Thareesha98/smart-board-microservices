package com.sbms.sbms_backend.service;

import com.sbms.sbms_backend.client.BoardingClient;
import com.sbms.sbms_backend.dto.dashboard.StudentBoardingDashboardDTO;
import com.sbms.sbms_backend.dto.payment.PaymentResult;
import com.sbms.sbms_backend.dto.registration.*;
import com.sbms.sbms_backend.mapper.RegistrationMapper;
import com.sbms.sbms_backend.mapper.StudentBoardingDashboardMapper;
import com.sbms.sbms_backend.model.Registration;
import com.sbms.sbms_backend.model.User;
import com.sbms.sbms_backend.model.enums.PaymentMethod;
import com.sbms.sbms_backend.model.enums.RegistrationStatus;
import com.sbms.sbms_backend.record.BoardingSnapshot;
import com.sbms.sbms_backend.repository.RegistrationRepository;
import com.sbms.sbms_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BoardingClient boardingClient;

    @Autowired
    private PaymentService paymentService;

    // -------------------------------------------------
    // REGISTER
    // -------------------------------------------------
    public RegistrationResponseDTO register(Long studentId, RegistrationRequestDTO dto) {

        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        BoardingSnapshot boarding =
                boardingClient.getBoarding(dto.getBoardingId());

        if (boarding.availableSlots() < dto.getNumberOfStudents()) {
            throw new RuntimeException("Not enough slots available");
        }

        PaymentResult result = paymentService.processPayment(
                studentId,
                boarding.keyMoney(),
                PaymentMethod.CARD
        );

        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }

        Registration r = new Registration();
        r.setBoardingId(boarding.id());
        r.setStudent(student);
        r.setNumberOfStudents(dto.getNumberOfStudents());
        r.setStudentNote(dto.getStudentNote());
        r.setStatus(RegistrationStatus.PENDING);
        r.setKeyMoneyPaid(true);
        r.setPaymentTransactionRef(result.getTransactionId());

        registrationRepo.save(r);

        return RegistrationMapper.toDTO(
                r,
                boarding.title(),
                boarding.keyMoney(),
                boarding.pricePerMonth()
        );
    }

    // -------------------------------------------------
    // STUDENT REGISTRATIONS
    // -------------------------------------------------
    public List<RegistrationResponseDTO> getStudentRegistrations(Long studentId) {

        return registrationRepo.findByStudentId(studentId)
                .stream()
                .map(r -> {
                    BoardingSnapshot b = boardingClient.getBoarding(r.getBoardingId());
                    return RegistrationMapper.toDTO(
                            r,
                            b.title(),
                            b.keyMoney(),
                            b.pricePerMonth()
                    );
                })
                .toList();
    }

    // -------------------------------------------------
    // OWNER REGISTRATIONS
    // -------------------------------------------------
    public List<RegistrationResponseDTO> getOwnerRegistrations(
            Long ownerId,
            RegistrationStatus status
    ) {

        List<Long> boardingIds =
                boardingClient.getBoardingIdsByOwner(ownerId);

        return registrationRepo
                .findByBoardingIdsAndStatus(boardingIds, status)
                .stream()
                .map(r -> {
                    BoardingSnapshot b = boardingClient.getBoarding(r.getBoardingId());
                    return RegistrationMapper.toDTO(
                            r,
                            b.title(),
                            b.keyMoney(),
                            b.pricePerMonth()
                    );
                })
                .toList();
    }

    // -------------------------------------------------
    // OWNER DECISION
    // -------------------------------------------------
    public RegistrationResponseDTO decide(
            Long ownerId,
            Long regId,
            RegistrationDecisionDTO dto
    ) {

        Registration r = registrationRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        Long actualOwnerId =
                boardingClient.getOwnerId(r.getBoardingId());

        if (!actualOwnerId.equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }

        r.setStatus(dto.getStatus());
        r.setOwnerNote(dto.getOwnerNote());

        if (dto.getStatus() == RegistrationStatus.APPROVED) {
            boardingClient.reserveSlots(
                    r.getBoardingId(),
                    r.getNumberOfStudents()
            );
        }

        registrationRepo.save(r);

        BoardingSnapshot b = boardingClient.getBoarding(r.getBoardingId());

        return RegistrationMapper.toDTO(
                r,
                b.title(),
                b.keyMoney(),
                b.pricePerMonth()
        );
    }

    // -------------------------------------------------
    // STUDENT DASHBOARD
    // -------------------------------------------------
    public StudentBoardingDashboardDTO getDashboard(
            Long regId,
            Long studentId
    ) {

        Registration reg = registrationRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!reg.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Forbidden");
        }

        BoardingSnapshot b =
                boardingClient.getBoarding(reg.getBoardingId());

        return StudentBoardingDashboardMapper.toDTO(
                reg,
                b,
                b.pricePerMonth(),
                "PENDING",
                null,
                0,
                0,
                null,
                0.0,
                false
        );
    }
    
 // -------------------------------------------------
 // CANCEL REGISTRATION (STUDENT)
 // -------------------------------------------------
 public RegistrationResponseDTO cancel(Long studentId, Long regId) {

     Registration r = registrationRepo.findById(regId)
             .orElseThrow(() -> new RuntimeException("Registration not found"));

     if (!r.getStudent().getId().equals(studentId)) {
         throw new RuntimeException("Unauthorized");
     }

     if (r.getStatus() == RegistrationStatus.APPROVED) {
         throw new RuntimeException("Cannot cancel approved registration");
     }

     r.setStatus(RegistrationStatus.CANCELLED);
     registrationRepo.save(r);

     // 🔹 Fetch boarding snapshot for DTO
     var boarding = boardingClient.getBoarding(r.getBoardingId());

     return RegistrationMapper.toDTO(
             r,
             boarding.title(),
             boarding.keyMoney(),
             boarding.pricePerMonth()
     );
 }

}
