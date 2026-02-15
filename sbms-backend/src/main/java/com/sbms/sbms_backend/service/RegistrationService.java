/*package com.sbms.sbms_backend.service;

import com.sbms.sbms_backend.mapper.RegistrationMapper;
import com.sbms.sbms_backend.mapper.StudentBoardingDashboardMapper;
import com.sbms.sbms_backend.dto.dashboard.StudentBoardingDashboardDTO;
import com.sbms.sbms_backend.dto.payment.PaymentResult;
import com.sbms.sbms_backend.dto.registration.*;
import com.sbms.sbms_backend.model.Boarding;
import com.sbms.sbms_backend.model.Registration;
import com.sbms.sbms_backend.model.User;
import com.sbms.sbms_backend.model.enums.PaymentMethod;
import com.sbms.sbms_backend.model.enums.RegistrationStatus;
import com.sbms.sbms_backend.model.enums.Status;
import com.sbms.sbms_backend.model.enums.UserRole;
import com.sbms.sbms_backend.repository.BoardingRepository;
import com.sbms.sbms_backend.repository.RegistrationRepository;
import com.sbms.sbms_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepo;

    @Autowired
    private BoardingRepository boardingRepo;

    @Autowired
    private UserRepository userRepo;
    
    

    @Autowired
    private PaymentService paymentService;
    public RegistrationResponseDTO register(Long studentId, RegistrationRequestDTO dto) {

        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Boarding boarding = boardingRepo.findById(dto.getBoardingId())
                .orElseThrow(() -> new RuntimeException("Boarding not found"));

        if (boarding.getAvailable_slots() < dto.getNumberOfStudents()) {
            throw new RuntimeException("Not enough slots available");
        }

        if (!dto.isKeyMoneyPaid()) {
            throw new RuntimeException("Key money must be paid to register");
        }

        PaymentResult result = paymentService.processPayment(
                studentId,
                boarding.getKeyMoney(),
                PaymentMethod.CARD   // simulated
        );

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "Key money payment failed: " + result.getMessage()
            );
        }
        
       



        Registration r = new Registration();
        r.setBoarding(boarding);
        r.setStudent(student);
        r.setNumberOfStudents(dto.getNumberOfStudents());
        r.setStudentNote(dto.getStudentNote());
        r.setStatus(RegistrationStatus.PENDING);
        r.setKeyMoneyPaid(true);
        
        r.setPaymentTransactionRef(result.getTransactionId());

        registrationRepo.save(r);

        return RegistrationMapper.toDTO(r);
    }


    public List<RegistrationResponseDTO> getStudentRegistrations(Long studentId) {
        return registrationRepo.findByStudentId(studentId)
                .stream().map(RegistrationMapper::toDTO)
                .toList();
    }

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

        return RegistrationMapper.toDTO(r);
    }

    public List<RegistrationResponseDTO> getOwnerRegistrations(Long ownerId, RegistrationStatus status) {

        return registrationRepo.findByBoardingOwnerId(ownerId, status)
                .stream()
                .map(RegistrationMapper::toDTO)
                .toList();
    }

    public RegistrationResponseDTO decide(Long ownerId, Long regId, RegistrationDecisionDTO dto) {

        Registration r = registrationRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!r.getBoarding().getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }

        r.setStatus(dto.getStatus());
        r.setOwnerNote(dto.getOwnerNote());

        if (dto.getStatus() == RegistrationStatus.APPROVED) {
            Boarding b = r.getBoarding();
            b.setAvailable_slots(b.getAvailable_slots() - r.getNumberOfStudents());
            boardingRepo.save(b);
        }

        registrationRepo.save(r);

        return RegistrationMapper.toDTO(r);
    }
    
    public StudentBoardingDashboardDTO getDashboard(Long regId, Long loggedStudentId) {

        Registration reg = registrationRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!reg.getStudent().getId().equals(loggedStudentId)) {
            throw new RuntimeException("Forbidden");
        }

        BigDecimal currentMonthDue = reg.getBoarding().getPricePerMonth();
        String paymentStatus = "PENDING";
        LocalDate lastPaymentDate = null;

        int openIssues = 0;
        int resolvedIssues = 0;
        LocalDate lastIssueDate = null;

       
        Double avgRating = 0.0;
        boolean reviewSubmitted = false;

        return StudentBoardingDashboardMapper.toDTO(
                reg,
                currentMonthDue,
                paymentStatus,
                lastPaymentDate,
                openIssues,
                resolvedIssues,
                lastIssueDate,
                avgRating,
                reviewSubmitted
        );
    }

    
}  */







package com.sbms.sbms_backend.service;

import com.sbms.sbms_backend.client.BoardingClient;
import com.sbms.sbms_backend.client.UserClient;
import com.sbms.sbms_backend.dto.agreement.AgreementPdfResult;
import com.sbms.sbms_backend.dto.boarding.BoardingFullSnapshot;
import com.sbms.sbms_backend.dto.dashboard.StudentBoardingDashboardDTO;
import com.sbms.sbms_backend.dto.registration.*;
import com.sbms.sbms_backend.mapper.RegistrationMapper;
import com.sbms.sbms_backend.mapper.StudentBoardingDashboardMapper;
import com.sbms.sbms_backend.model.PaymentIntent;
import com.sbms.sbms_backend.model.Registration;
import com.sbms.sbms_backend.model.enums.PaymentIntentStatus;
import com.sbms.sbms_backend.model.enums.PaymentType;
import com.sbms.sbms_backend.model.enums.RegistrationStatus;
import com.sbms.sbms_backend.record.BoardingSnapshot;
import com.sbms.sbms_backend.repository.PaymentIntentRepository;
import com.sbms.sbms_backend.repository.RegistrationRepository;
import com.sbms.sbms_backend.dto.registration.RegistrationDecisionDTO;
import com.sbms.sbms_backend.dto.registration.RegistrationRequestDTO;
import com.sbms.sbms_backend.dto.registration.RegistrationResponseDTO;
import com.sbms.sbms_backend.dto.user.UserMinimalDTO;
import com.sbms.sbms_backend.dto.user.UserSnapshotDTO;
import com.sbms.sbms_backend.repository.ReviewRepository;
import com.sbms.sbms_backend.service.AgreementBlockchainService;
import com.sbms.sbms_backend.service.AgreementPdfService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired private RegistrationRepository registrationRepo;
    @Autowired private PaymentIntentRepository paymentIntentRepo;
    @Autowired private AgreementPdfService agreementPdfService;
    @Autowired private AgreementBlockchainService agreementBlockchainService;
    
    @Autowired
    private UserClient userClient;

    
    @Autowired
    private BoardingClient boardingClient;

    
    @Autowired
    private ReviewRepository reviewRepo;


    @Transactional
    public RegistrationResponseDTO register(Long studentId, RegistrationRequestDTO dto) {

        // 1. Fetch Boarding details via Client
        BoardingSnapshot boarding = boardingClient.getBoarding(dto.getBoardingId());

        // 2. Fetch Payment Intent (Source of Truth)
        PaymentIntent intent = paymentIntentRepo
                .findTopByStudentIdAndBoardingIdAndTypeOrderByCreatedAtDesc(
                        studentId,
                        boarding.id(),
                        PaymentType.KEY_MONEY
                )
                .orElseThrow(() -> new RuntimeException("Key money payment required"));

        // Check payment status
        boolean keyMoneyPaid = intent.getStatus() == PaymentIntentStatus.SUCCESS
                 || intent.getStatus() == PaymentIntentStatus.AWAITING_MANUAL_APPROVAL;

        if (!keyMoneyPaid) {
            throw new RuntimeException("Key money not paid");
        }

        // 3. Create Registration Entity
        Registration r = new Registration();
        r.setBoardingId(boarding.id());
        
        // Fix: Store the studentId, not a Student entity
        r.setStudentId(studentId); 
        
        r.setNumberOfStudents(dto.getNumberOfStudents());
        r.setStudentNote(dto.getStudentNote());
        r.setMoveInDate(dto.getMoveInDate());
        r.setContractDuration(dto.getContractDuration());
        r.setEmergencyContactName(dto.getEmergencyContact());
        r.setSpecialRequirements(dto.getSpecialRequirements());
        r.setStudentSignatureBase64(dto.getStudentSignatureBase64());

        r.setKeyMoneyPaid(true);
        r.setPaymentMethod(intent.getMethod().name());
        r.setPaymentTransactionRef(intent.getReferenceId());
        r.setStatus(RegistrationStatus.PENDING);

        registrationRepo.save(r);

        // 4. Fetch Student Info for the Mapper
        UserMinimalDTO studentDetails = userClient.getUserMinimal(studentId);

        // 5. Return mapped DTO with 4 arguments
        return RegistrationMapper.toDTO(
            r, 
            boarding.keyMoney(), 
            boarding.pricePerMonth(),
            studentDetails
        );
    }

    // ================= STUDENT VIEWS =================
    public List<RegistrationResponseDTO> getStudentRegistrations(Long studentId) {
        // 1. Get all registrations for the student
        List<Registration> registrations = registrationRepo.findByStudentId(studentId);

        if (registrations.isEmpty()) {
            return List.of();
        }

        // 2. Fetch the Student details ONCE (since all registrations belong to this ID)
        UserMinimalDTO student = userClient.getUserMinimal(studentId);

        // 3. Extract unique boarding IDs and fetch snapshots in batch
        List<Long> boardingIds = registrations.stream()
                .map(Registration::getBoardingId)
                .distinct()
                .toList();

        List<BoardingSnapshot> snapshots = boardingClient.getBoardingSnapshots(boardingIds);

        // 4. Map them together using the 4-argument mapper
        return registrations.stream()
                .map(r -> {
                    BoardingSnapshot s = snapshots.stream()
                            .filter(snap -> snap.id().equals(r.getBoardingId()))
                            .findFirst()
                            .orElse(null);

                    return RegistrationMapper.toDTO(
                        r, 
                        s != null ? s.keyMoney() : BigDecimal.ZERO, 
                        s != null ? s.pricePerMonth() : BigDecimal.ZERO,
                        student, // The 4th argument: Student details 
                        s != null ? s.title(): null
                    );
                })
                .toList();
    }
    
    public RegistrationResponseDTO cancel(Long studentId, Long regId) {

        Registration r = registrationRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        // 1. Security Check
        if (!r.getStudentId().equals(studentId)) {
            throw new RuntimeException("Unauthorized");
        }

        // 2. Business Logic Check
        if (r.getStatus() == RegistrationStatus.APPROVED) {
            throw new RuntimeException("Cannot cancel approved registration");
        }

        // 3. Update Status
        r.setStatus(RegistrationStatus.CANCELLED);
        registrationRepo.save(r);

        // 4. Fetch Boarding Data for the Mapper
        // We need the snapshot to get the keyMoney and monthlyPrice for the DTO
        BoardingSnapshot boarding = boardingClient.getBoarding(r.getBoardingId());

        UserMinimalDTO student = userClient.getUserMinimal(r.getStudentId()); // Added this call
        
        return RegistrationMapper.toDTO(
            r, 
            boarding.keyMoney(), 
            boarding.pricePerMonth(),
            student
        );
    }
    // ================= OWNER VIEWS =================

    public List<RegistrationResponseDTO> getOwnerRegistrations(
            Long ownerId,
            RegistrationStatus status
    ) {
        List<Long> boardingIds = boardingClient.getBoardingIdsByOwner(ownerId);

        if (boardingIds.isEmpty()) {
            return List.of();
        }

        List<Registration> registrations;

        if (status == null) {
            registrations = registrationRepo.findByBoardingIdIn(boardingIds);
        } else {
            registrations = registrationRepo.findByBoardingIdInAndStatus(
                    boardingIds,
                    status
            );
        }

        if (registrations.isEmpty()) {
            return List.of();
        }

        // Batch fetch snapshots
        List<BoardingSnapshot> snapshots =
                boardingClient.getBoardingSnapshots(boardingIds);

        return registrations.stream()
                .map(r -> {
                    BoardingSnapshot s = snapshots.stream()
                            .filter(b -> b.id().equals(r.getBoardingId()))
                            .findFirst()
                            .orElse(null);

                    UserMinimalDTO student =
                            userClient.getUserMinimal(r.getStudentId());

                    return RegistrationMapper.toDTO(
                            r,
                            s != null ? s.keyMoney() : BigDecimal.ZERO,
                            s != null ? s.pricePerMonth() : BigDecimal.ZERO,
                            student
                    );
                })
                .toList();
    }


    // ================= OWNER DECISION =================

    @Transactional
    public RegistrationResponseDTO decide(Long ownerId, Long regId, RegistrationDecisionDTO dto) {

        Registration r = registrationRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        // 1. Fetch Boarding details to verify ownership
        BoardingSnapshot boardingBasic = boardingClient.getBoarding(r.getBoardingId());
        
        if (!boardingBasic.ownerId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }

        // 2. Fetch payment intent (source of truth)
        PaymentIntent intent = paymentIntentRepo
                .findTopByStudentIdAndBoardingIdAndTypeOrderByCreatedAtDesc(
                        r.getStudentId(),
                        r.getBoardingId(),
                        PaymentType.KEY_MONEY
                )
                .orElseThrow(() -> new RuntimeException("Key money payment missing"));

        // 3. Payment Validation Gate
        if (dto.getStatus() == RegistrationStatus.APPROVED) {
            if (intent.getStatus() == PaymentIntentStatus.AWAITING_MANUAL_APPROVAL
                    && !dto.isApproveWithPendingPayment()) {
                throw new RuntimeException("Payment is not verified. Owner must confirm override.");
            }

            if (intent.getStatus() != PaymentIntentStatus.SUCCESS
                    && intent.getStatus() != PaymentIntentStatus.AWAITING_MANUAL_APPROVAL) {
                throw new RuntimeException("Registration cannot be approved without payment");
            }
        }

        // 4. Apply Decision
        r.setStatus(dto.getStatus());
        r.setOwnerNote(dto.getOwnerNote());

        // 5. Logic for APPROVED status
        if (dto.getStatus() == RegistrationStatus.APPROVED) {
            if (dto.getOwnerSignatureBase64() == null) {
                throw new RuntimeException("Owner signature required");
            }

            r.setOwnerSignatureBase64(dto.getOwnerSignatureBase64());

            // --- PDF GENERATION PREPARATION ---
            // Fetch required external data for the PDF service
            UserMinimalDTO student = userClient.getUserMinimal(r.getStudentId());
            BoardingFullSnapshot boardingFull = boardingClient.getBoardingFull(r.getBoardingId());

            // Call the 3-argument PDF service
            AgreementPdfResult result = agreementPdfService.generateAndUploadAgreement(
                    r, 
                    student, 
                    boardingFull
            );

            r.setAgreementPdfPath(result.getPdfUrl());
            r.setAgreementHash(result.getPdfHash());

            // Blockchain integration
            agreementBlockchainService.addAgreementBlock(
                    r.getId(),
                    result.getPdfHash()
            );

            // Update slots in Boarding service
            boardingClient.reserveSlots(r.getBoardingId(), r.getNumberOfStudents());
        }

        registrationRepo.save(r);
        
        // 6. Final DTO Mapping
        // Since we already fetched the student for the PDF, we reuse it here
        UserMinimalDTO studentForDto = userClient.getUserMinimal(r.getStudentId());
        
        return RegistrationMapper.toDTO(
                r, 
                boardingBasic.keyMoney(), 
                boardingBasic.pricePerMonth(),
                studentForDto
        );
    }

    
    
    
    @Transactional(readOnly = true)
    public StudentBoardingDashboardDTO getDashboard(Long regId, Long loggedStudentId) {

        // 1. Fetch registration (NO user entity inside)
        Registration reg = registrationRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        // 2. Authorization check using IDs only
        if (!reg.getStudentId().equals(loggedStudentId)) {
            throw new RuntimeException("Forbidden");
        }

        // 3. Fetch boarding snapshot (already microservice-safe)
        BoardingFullSnapshot boarding =
                boardingClient.getBoardingFull(reg.getBoardingId());

        // 4. Fetch STUDENT snapshot from user-service
        UserSnapshotDTO student =
                userClient.getUserSnapshot(loggedStudentId);

        // 5. Fetch OWNER snapshot (from boarding.ownerId)
        UserSnapshotDTO owner =
                userClient.getUserSnapshot(boarding.ownerId());

        // 6. Dashboard calculated values
        BigDecimal currentMonthDue = boarding.pricePerMonth();
        String paymentStatus = "PENDING";
        LocalDate lastPaymentDate = null;
        int openIssues = 0;
        int resolvedIssues = 0;
        LocalDate lastIssueDate = null;

        Double avg = reviewRepo.getAverageRatingForBoarding(reg.getBoardingId());
        Double avgRating = (avg != null)
                ? Math.round(avg * 10.0) / 10.0
                : 0.0;

        int reviewCount =
                reviewRepo.countByBoardingId(reg.getBoardingId());

        boolean reviewSubmitted =
                reviewRepo.existsByStudentIdAndBoardingId(
                        loggedStudentId,
                        reg.getBoardingId()
                );

        // 7. Base DTO mapping
        StudentBoardingDashboardDTO dto =
                StudentBoardingDashboardMapper.toDTO(
                        reg,
                        boarding,
                        currentMonthDue,
                        paymentStatus,
                        lastPaymentDate,
                        openIssues,
                        resolvedIssues,
                        lastIssueDate,
                        avgRating,
                        reviewSubmitted
                );

        // 8. Active members list (STUDENT SNAPSHOTS)
        List<Registration> activeRegistrations =
                registrationRepo.findByBoardingIdAndStatus(
                        reg.getBoardingId(),
                        RegistrationStatus.APPROVED
                );

        List<StudentBoardingDashboardDTO.MemberDTO> members =
                activeRegistrations.stream()
                        .map(r -> {
                            UserSnapshotDTO memberUser =
                                    userClient.getUserSnapshot(r.getStudentId());

                            StudentBoardingDashboardDTO.MemberDTO m =
                                    new StudentBoardingDashboardDTO.MemberDTO();

                            m.setId(memberUser.getId());
                            m.setName(memberUser.getFullName());
                            m.setPhone(memberUser.getPhone());
                            m.setJoinedDate(
                                    r.getCreatedAt().toLocalDate().toString()
                            );
                            m.setAvatar(memberUser.getProfileImageUrl());

                            return m;
                        })
                        .collect(Collectors.toList());

        dto.setMembers(members);

        // 9. Boarding visuals
        if (boarding.imageUrls() != null &&
            !boarding.imageUrls().isEmpty()) {

            dto.setBoardingImage(boarding.imageUrls().get(0));
        }

        dto.setBoardingCreatedDate(
                boarding.createdAt() != null
                        ? boarding.createdAt().toLocalDate().toString()
                        : LocalDate.now().toString()
        );

        // 10. Owner info (FROM USER-SERVICE)
        dto.setOwnerId(owner.getId());
        dto.setOwnerName(owner.getFullName());
        dto.setOwnerProfileImage(owner.getProfileImageUrl());
        dto.setOwnerEmail(owner.getEmail());
        dto.setOwnerPhone(owner.getPhone());

        dto.setAverageRating(avgRating);
        dto.setReviewCount(reviewCount);

        return dto;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void approveLeave(Long ownerId, Long regId) {
        Registration r = registrationRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        BoardingSnapshot boarding = boardingClient.getBoarding(r.getBoardingId());

        if (!boarding.ownerId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized: You do not own this property.");
        }

        if (r.getStatus() == RegistrationStatus.LEAVE_REQUESTED || r.getStatus() == RegistrationStatus.APPROVED) {
            
         
            int releaseCount = -r.getNumberOfStudents(); 
            boardingClient.reserveSlots(r.getBoardingId(), releaseCount);

            r.setStatus(RegistrationStatus.LEFT);
            registrationRepo.save(r);
        } else {
            throw new RuntimeException("Cannot approve leave for status: " + r.getStatus());
        }
    }
    
    @Transactional
    public void requestLeave(Long studentId, Long regId) {
        Registration r = registrationRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!r.getStudentId().equals(studentId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (r.getStatus() != RegistrationStatus.APPROVED) {
            throw new RuntimeException("You can only request leave if you are currently approved.");
        }

        r.setStatus(RegistrationStatus.LEAVE_REQUESTED);
        registrationRepo.save(r);
    }


}
