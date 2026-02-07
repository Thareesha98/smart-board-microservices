package com.sbms.sbms_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbms.sbms_backend.client.BoardingClient;
import com.sbms.sbms_backend.client.UserClient;
import com.sbms.sbms_backend.dto.billing.MonthlyBillResponseDTO;
import com.sbms.sbms_backend.dto.user.UserMinimalDTO;
import com.sbms.sbms_backend.mapper.MonthlyBillMapper;
import com.sbms.sbms_backend.model.MonthlyBill;
import com.sbms.sbms_backend.model.Registration;
import com.sbms.sbms_backend.model.UtilityBill;
import com.sbms.sbms_backend.model.enums.BillDueStatus;
import com.sbms.sbms_backend.model.enums.MonthlyBillStatus;
import com.sbms.sbms_backend.model.enums.RegistrationStatus;
import com.sbms.sbms_backend.record.BoardingSnapshot;
import com.sbms.sbms_backend.repository.MonthlyBillRepository;
import com.sbms.sbms_backend.repository.RegistrationRepository;
import com.sbms.sbms_backend.repository.UtilityBillRepository;

import jakarta.transaction.Transactional;

@Service
public class MonthlyBillService {

    @Autowired
    private MonthlyBillRepository billRepo;

    @Autowired
    private UtilityBillRepository utilityRepo;

    @Autowired
    private RegistrationRepository registrationRepo;
    
    @Autowired
    private BoardingClient boardingClient;
    
    @Autowired
    private UserClient userClient;

   
    
    @Transactional
    public void generateBillsForMonth(String month) {

        // 1. Get utility snapshots for the month (Local records)
        List<UtilityBill> utilities = utilityRepo.findByMonth(month);

        // Collect all boarding IDs we need data for to avoid N+1 API calls
        List<Long> boardingIds = utilities.stream()
                .map(UtilityBill::getBoardingId)
                .distinct()
                .toList();

        // 2. Batch fetch Boarding details from Boarding Service
        List<BoardingSnapshot> snapshots = boardingClient.getBoardingSnapshots(boardingIds);

        for (UtilityBill utility : utilities) {
            
            // Find the specific boarding price from our batch-fetched snapshots
            BoardingSnapshot boarding = snapshots.stream()
                    .filter(s -> s.id().equals(utility.getBoardingId()))
                    .findFirst()
                    .orElse(null);

            if (boarding == null) continue;

            // 3. Get all APPROVED registrations for this boarding (Local DB)
            // Note: Changed from findByBoarding_IdAndStatus to findByBoardingIdAndStatus
            List<Registration> registrations = registrationRepo.findByBoardingIdAndStatus(
                    boarding.id(),
                    RegistrationStatus.APPROVED
            );

            if (registrations.isEmpty()) continue;

            int studentCount = registrations.size();

            // 4. Split RENT and UTILITIES per student
            BigDecimal boardingFeePerStudent = boarding.pricePerMonth()
                    .divide(BigDecimal.valueOf(studentCount), 2, RoundingMode.HALF_UP);

            BigDecimal electricityPerStudent = utility.getElectricityAmount()
                    .divide(BigDecimal.valueOf(studentCount), 2, RoundingMode.HALF_UP);

            BigDecimal waterPerStudent = utility.getWaterAmount()
                    .divide(BigDecimal.valueOf(studentCount), 2, RoundingMode.HALF_UP);

            // 5. Generate bill per registration
            for (Registration reg : registrations) {

                // 🔒 Prevent duplicate bills (Using IDs, not Entities)
                boolean exists = billRepo
                        .findByStudentIdAndBoardingIdAndMonth(
                                reg.getStudentId(),
                                boarding.id(),
                                month
                        )
                        .isPresent();

                if (exists) continue;

                // 6. Total calculation
                BigDecimal total = boardingFeePerStudent
                        .add(electricityPerStudent)
                        .add(waterPerStudent);

                // 7. Create Monthly Bill (Local Entity)
                MonthlyBill bill = new MonthlyBill();
                bill.setStudentId(reg.getStudentId()); // Store ID
                bill.setBoardingId(boarding.id());    // Store ID
                bill.setMonth(month);

                bill.setBoardingFee(boardingFeePerStudent);
                bill.setElectricityFee(electricityPerStudent);
                bill.setWaterFee(waterPerStudent);
                bill.setTotalAmount(total);

                bill.setStatus(MonthlyBillStatus.UNPAID);
                
                // Safe Date Parsing
                bill.setDueDate(LocalDate.parse(month + "-10"));

                billRepo.save(bill);
            }
        }
    }


    
    
    public List<MonthlyBillResponseDTO> getForStudent(Long studentId) {
        // 1. Fetch student info once
        UserMinimalDTO student = userClient.getUserMinimal(studentId);
        String studentName = (student != null) ? student.getFullName() : "Unknown Student";

        // 2. Fetch bills
        List<MonthlyBill> bills = billRepo.findByStudentId(studentId);

        // 3. To avoid N+1 calls for ownerId, fetch all boarding snapshots for these bills
        List<Long> boardingIds = bills.stream().map(MonthlyBill::getBoardingId).distinct().toList();
        List<BoardingSnapshot> snapshots = boardingClient.getBoardingSnapshots(boardingIds);

        return bills.stream()
                .map(bill -> {
                    // Find the snapshot to get the ownerId
                    BoardingSnapshot s = snapshots.stream()
                            .filter(snap -> snap.id().equals(bill.getBoardingId()))
                            .findFirst()
                            .orElse(null);
                    
                    Long ownerId = (s != null) ? s.ownerId() : null;

                    return MonthlyBillMapper.toDTO(
                            bill,
                            getDueStatus(bill),
                            getDueInDays(bill),
                            ownerId,
                            studentName
                    );
                })
                .toList();
    }

    public List<MonthlyBillResponseDTO> getForOwner(Long ownerId) {
        // 1. Get Boarding IDs for this owner
        List<Long> boardingIds = boardingClient.getBoardingIdsByOwner(ownerId);

        if (boardingIds.isEmpty()) return List.of();

        // 2. Fetch all bills for these boardings
        List<MonthlyBill> bills = billRepo.findByBoardingIdIn(boardingIds);

        // 3. Batch fetch student names to avoid N+1 API calls inside the loop
        List<Long> studentIds = bills.stream().map(MonthlyBill::getStudentId).distinct().toList();
        // Assuming you have a batch method in userClient, otherwise fetch individually or map locally
        // For now, let's assume we fetch them to provide the studentName
        
        return bills.stream()
                .map(bill -> {
                    // Fetch student name (Ideally use a batch fetch for performance)
                    UserMinimalDTO student = userClient.getUserMinimal(bill.getStudentId());
                    String studentName = (student != null) ? student.getFullName() : "Unknown";

                    return MonthlyBillMapper.toDTO(
                            bill,
                            getDueStatus(bill),
                            getDueInDays(bill),
                            ownerId, // Already have this from the method argument
                            studentName
                    );
                })
                .toList();
    }
    
    
    public BillDueStatus getDueStatus(MonthlyBill bill) {

        if (bill.getStatus() == MonthlyBillStatus.PAID) {
            return BillDueStatus.PAID;
        }

        LocalDate today = LocalDate.now();

        if (today.isAfter(bill.getDueDate())) {
            return BillDueStatus.OVERDUE;
        }

        return BillDueStatus.DUE_SOON;
    }

    public int getDueInDays(MonthlyBill bill) {

        if (bill.getStatus() == MonthlyBillStatus.PAID) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(
                LocalDate.now(),
                bill.getDueDate()
        );
    }

    
    
    
}
