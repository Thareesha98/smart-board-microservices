package com.sbms.sbms_payment_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sbms.sbms_payment_service.client.RegistrationClient;
import com.sbms.sbms_payment_service.client.RegistrationClient.ApprovedRegistrationDTO;
import com.sbms.sbms_payment_service.entity.MonthlyBill;
import com.sbms.sbms_payment_service.entity.UtilityBill;
import com.sbms.sbms_payment_service.entity.enums.MonthlyBillStatus;
import com.sbms.sbms_payment_service.repository.MonthlyBillRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlyBillService {

    private final MonthlyBillRepository billRepo;
    private final RegistrationClient registrationClient;

    @Transactional
    public void generateBillsForUtility(UtilityBill utility) {

        Long boardingId = utility.getBoardingId();
        Long ownerId = utility.getOwnerId();
        String month = utility.getMonth();

        // 🔑 Only registered students returned
        List<ApprovedRegistrationDTO> students =
                registrationClient.getApprovedRegistrations(boardingId);

        for (ApprovedRegistrationDTO reg : students) {

            Long studentId = reg.studentId();

            boolean exists = billRepo
                    .existsByStudentIdAndBoardingIdAndMonth(
                            studentId, boardingId, month
                    );

            if (exists) continue;

            BigDecimal boardingFee = reg.monthlyRent();
            BigDecimal electricity = utility.getElectricityAmount();
            BigDecimal water = utility.getWaterAmount();

            MonthlyBill bill = new MonthlyBill();
            bill.setStudentId(studentId);
            bill.setBoardingId(boardingId);
            bill.setOwnerId(ownerId);
            bill.setMonth(month);
            bill.setBoardingFee(boardingFee);
            bill.setElectricityFee(electricity);
            bill.setWaterFee(water);
            bill.setTotalAmount(
                    boardingFee.add(electricity).add(water)
            );
            bill.setStatus(MonthlyBillStatus.UNPAID);
            bill.setDueDate(LocalDate.parse(month + "-10"));

            billRepo.save(bill);
        }
    }
}
