package com.sbms.sbms_registration_service.service;

import com.sbms.sbms_registration_service.dto.RegistrationRequestDTO;
import com.sbms.sbms_registration_service.dto.RegistrationResponseDTO;







import com.sbms.sbms_registration_service.dto.StudentBoardingDashboardDTO;
import com.sbms.sbms_registration_service.dto.external.BoardingClient;
import com.sbms.sbms_registration_service.dto.external.BoardingSnapshot;
import com.sbms.sbms_registration_service.enums.RegistrationStatus;
import com.sbms.sbms_registration_service.mapper.StudentBoardingDashboardMapper;
import com.sbms.sbms_registration_service.model.Registration;
import com.sbms.sbms_registration_service.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepo;
    private final BoardingClient boardingClient;
    private final PaymentService paymentService;

    public RegistrationService(
            RegistrationRepository registrationRepo,
            BoardingClient boardingClient,
            PaymentService paymentService
    ) {
        this.registrationRepo = registrationRepo;
        this.boardingClient = boardingClient;
        this.paymentService = paymentService;
    }

    // -------------------------------------------------
    // REGISTER (STUDENT)
    // -------------------------------------------------
    public RegistrationResponseDTO register(
            Long studentId,
            RegistrationRequestDTO dto,
            Long requesterId,
            String role
    ) {

        validateStudentAccess(studentId, requesterId, role);

        BoardingSnapshot boarding =
                boardingClient.getSnapshot(dto.getBoardingId());

        if (boarding.availableSlots() < dto.getNumberOfStudents()) {
            throw new IllegalStateException("Not enough slots available");
        }

        PaymentResult payment =
                paymentService.processKeyMoney(
                        studentId,
                        boarding.keyMoney()
                );

        if (!payment.isSuccess()) {
            throw new IllegalStateException(payment.getMessage());
        }

        Registration r = new Registration();
        r.setBoardingId(boarding.id());
        r.setStudentId(studentId);
        r.setOwnerId(boarding.ownerId());
        r.setNumberOfStudents(dto.getNumberOfStudents());
        r.setStudentNote(dto.getStudentNote());
        r.setStatus(RegistrationStatus.PENDING);
        r.setKeyMoneyPaid(true);
        r.setPaymentTransactionRef(payment.getTransactionId());

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
    @Transactional(readOnly = true)
    public List<RegistrationResponseDTO> getStudentRegistrations(
            Long studentId,
            Long requesterId,
            String role
    ) {

        validateStudentAccess(studentId, requesterId, role);

        return registrationRepo.findByStudentId(studentId)
                .stream()
                .map(r -> {
                    BoardingSnapshot b =
                            boardingClient.getSnapshot(r.getBoardingId());
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
    @Transactional(readOnly = true)
    public List<RegistrationResponseDTO> getOwnerRegistrations(
            Long ownerId,
            RegistrationStatus status,
            Long requesterId,
            String role
    ) {

        validateOwnerAccess(ownerId, requesterId, role);

        List<Long> boardingIds =
                boardingClient.getBoardingIdsByOwner(ownerId);

        return registrationRepo
                .findByBoardingIdsAndStatus(boardingIds, status)
                .stream()
                .map(r -> {
                    BoardingSnapshot b =
                            boardingClient.getSnapshot(r.getBoardingId());
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
    // OWNER DECISION (APPROVE / DECLINE)
    // -------------------------------------------------
    public RegistrationResponseDTO decide(
            Long ownerId,
            Long regId,
            RegistrationDecisionDTO dto,
            Long requesterId,
            String role
    ) {

        validateOwnerAccess(ownerId, requesterId, role);

        Registration r = registrationRepo.findById(regId)
                .orElseThrow(() -> new IllegalStateException("Registration not found"));

        if (!r.getOwnerId().equals(ownerId)) {
            throw new IllegalStateException("Unauthorized owner");
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

        BoardingSnapshot b =
                boardingClient.getSnapshot(r.getBoardingId());

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
    @Transactional(readOnly = true)
    public StudentBoardingDashboardDTO getDashboard(
            Long regId,
            Long studentId,
            Long requesterId,
            String role
    ) {

        validateStudentAccess(studentId, requesterId, role);

        Registration reg = registrationRepo.findById(regId)
                .orElseThrow(() -> new IllegalStateException("Registration not found"));

        if (!reg.getStudentId().equals(studentId)) {
            throw new IllegalStateException("Forbidden");
        }

        BoardingSnapshot boarding =
                boardingClient.getSnapshot(reg.getBoardingId());

        return StudentBoardingDashboardMapper.toDTO(
                reg,
                boarding,

                // payment-service (placeholders / future integration)
                BigDecimal.ZERO,
                0,
                "UNKNOWN",
                null,

                // issue-service
                0,
                0,
                null,

                // review-service
                null,
                false,

                // owner profile
                null
        );
    }

    // -------------------------------------------------
    // CANCEL REGISTRATION (STUDENT)
    // -------------------------------------------------
    public RegistrationResponseDTO cancel(
            Long studentId,
            Long regId,
            Long requesterId,
            String role
    ) {

        validateStudentAccess(studentId, requesterId, role);

        Registration r = registrationRepo.findById(regId)
                .orElseThrow(() -> new IllegalStateException("Registration not found"));

        if (!r.getStudentId().equals(studentId)) {
            throw new IllegalStateException("Unauthorized");
        }

        if (r.getStatus() == RegistrationStatus.APPROVED) {
            throw new IllegalStateException("Cannot cancel approved registration");
        }

        r.setStatus(RegistrationStatus.CANCELLED);
        registrationRepo.save(r);

        BoardingSnapshot boarding =
                boardingClient.getSnapshot(r.getBoardingId());

        return RegistrationMapper.toDTO(
                r,
                boarding.title(),
                boarding.keyMoney(),
                boarding.pricePerMonth()
        );
    }

    // -------------------------------------------------
    // ACCESS VALIDATION (NO SPRING SECURITY)
    // -------------------------------------------------
    private void validateStudentAccess(
            Long studentId,
            Long requesterId,
            String role
    ) {
        if (!"STUDENT".equals(role) || !studentId.equals(requesterId)) {
            throw new IllegalStateException("Forbidden");
        }
    }

    private void validateOwnerAccess(
            Long ownerId,
            Long requesterId,
            String role
    ) {
        if (!"OWNER".equals(role) || !ownerId.equals(requesterId)) {
            throw new IllegalStateException("Forbidden");
        }
    }
}
