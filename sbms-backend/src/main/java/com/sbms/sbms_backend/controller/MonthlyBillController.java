package com.sbms.sbms_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_backend.dto.billing.MonthlyBillResponseDTO;
import com.sbms.sbms_backend.service.MonthlyBillService;

@RestController
@RequestMapping("/api/bills")
public class MonthlyBillController {

    @Autowired
    private MonthlyBillService billService;

    @PostMapping("/generate/{month}")
    public String generate(@PathVariable String month) {
        billService.generateBillsForMonth(month);
        return "Monthly bills generated for " + month;
    }

    // STUDENT: View my bills
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public List<MonthlyBillResponseDTO> studentBills(
            @PathVariable Long studentId
    ) {
        return billService.getForStudent(studentId);
    }

    // OWNER: View bills
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
    public List<MonthlyBillResponseDTO> ownerBills(
            @PathVariable Long ownerId
    ) {
        return billService.getForOwner(ownerId);
    }
}
