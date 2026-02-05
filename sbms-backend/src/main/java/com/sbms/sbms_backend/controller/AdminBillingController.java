package com.sbms.sbms_backend.controller;




import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_backend.service.AutoUtilityBillService;
import com.sbms.sbms_backend.service.MonthlyBillService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/billing")
@RequiredArgsConstructor
public class AdminBillingController {

    private final AutoUtilityBillService autoUtilityService;
    private final MonthlyBillService monthlyBillService;

    @PostMapping("/generate/{month}")
    @PreAuthorize("hasRole('ADMIN')")
    public void generate(@PathVariable String month) {

        autoUtilityService.generateForMonth(month);
        monthlyBillService.generateBillsForMonth(month);
    }
}
