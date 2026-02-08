package com.sbms.sbms_payment_service.controller;


import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_payment_service.entity.MonthlyBill;
import com.sbms.sbms_payment_service.repository.MonthlyBillRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class MonthlyBillController {

    private final MonthlyBillRepository billRepo;

    @GetMapping("/student")
    public List<MonthlyBill> studentBills(
            @RequestHeader("X-User-Id") Long studentId
    ) {
        return billRepo.findByStudentId(studentId);
    }

    @GetMapping("/owner")
    public List<MonthlyBill> ownerBills(
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        return billRepo.findByOwnerId(ownerId);
    }
}
