package com.sbms.boarding_service.controller;

import com.sbms.boarding_service.dto.boarding.OwnerBoardingResponseDTO;
import com.sbms.boarding_service.service.AdminBoardingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boardings/admin")
@CrossOrigin
public class AdminBoardingController {

    private final AdminBoardingService adminBoardingService;

    public AdminBoardingController(AdminBoardingService adminBoardingService) {
        this.adminBoardingService = adminBoardingService;
    }

    // ----------------------------------------------------
    // GET ALL BOARDINGS (ANY STATUS)
    // ----------------------------------------------------
    @GetMapping
    public List<OwnerBoardingResponseDTO> getAll() {
        return adminBoardingService.getAllBoardings();
    }

    // ----------------------------------------------------
    // APPROVE BOARDING
    // ----------------------------------------------------
    @PutMapping("/{boardingId}/approve")
    public OwnerBoardingResponseDTO approve(
            @PathVariable Long boardingId
    ) {
        return adminBoardingService.approve(boardingId);
    }

    // ----------------------------------------------------
    // REJECT BOARDING
    // ----------------------------------------------------
    @PutMapping("/{boardingId}/reject")
    public OwnerBoardingResponseDTO reject(
            @PathVariable Long boardingId
    ) {
        return adminBoardingService.reject(boardingId);
    }

    // ----------------------------------------------------
    // DEACTIVATE BOARDING
    // ----------------------------------------------------
    @PutMapping("/{boardingId}/deactivate")
    public OwnerBoardingResponseDTO deactivate(
            @PathVariable Long boardingId
    ) {
        return adminBoardingService.deactivate(boardingId);
    }
}
