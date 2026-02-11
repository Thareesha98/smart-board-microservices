package com.sbms.boarding_service.controller;

import com.sbms.boarding_service.dto.boarding.OwnerBoardingResponseDTO;
import com.sbms.boarding_service.service.AdminBoardingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/boardings")
@CrossOrigin
@Tag(
    name = "Admin Boardings",
    description = "Admin-only APIs for approving, rejecting, and managing boardings"
)
@SecurityRequirement(name = "BearerAuth")
public class AdminBoardingController {

    private final AdminBoardingService adminBoardingService;

    public AdminBoardingController(AdminBoardingService adminBoardingService) {
        this.adminBoardingService = adminBoardingService;
    }

    // ----------------------------------------------------
    // GET ALL BOARDINGS (ANY STATUS)
    // ----------------------------------------------------
    @Operation(
        summary = "Get all boardings (admin)",
        description = "Returns all boardings regardless of approval status. "
                    + "Used by admins for moderation and review."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Boardings retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public List<OwnerBoardingResponseDTO> getAll() {
        return adminBoardingService.getAllBoardings();
    }

    // ----------------------------------------------------
    // APPROVE BOARDING
    // ----------------------------------------------------
    @Operation(
        summary = "Approve boarding",
        description = "Approves a boarding advertisement and makes it visible to students"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Boarding approved"),
        @ApiResponse(responseCode = "404", description = "Boarding not found")
    })
    @PutMapping("/{boardingId}/approve")
    public OwnerBoardingResponseDTO approve(
        @Parameter(description = "Boarding ID", example = "10")
        @PathVariable Long boardingId
    ) {
        return adminBoardingService.approve(boardingId);
    }

    // ----------------------------------------------------
    // REJECT BOARDING
    // ----------------------------------------------------
    @Operation(
        summary = "Reject boarding",
        description = "Rejects a boarding advertisement due to policy violations or invalid data"
    )
    @PutMapping("/{boardingId}/reject")
    public OwnerBoardingResponseDTO reject(
        @Parameter(description = "Boarding ID", example = "12")
        @PathVariable Long boardingId
    ) {
        return adminBoardingService.reject(boardingId);
    }

    // ----------------------------------------------------
    // DEACTIVATE BOARDING
    // ----------------------------------------------------
    @Operation(
        summary = "Deactivate boarding",
        description = "Temporarily disables a boarding listing after approval"
    )
    @PutMapping("/{boardingId}/deactivate")
    public OwnerBoardingResponseDTO deactivate(
        @Parameter(description = "Boarding ID", example = "15")
        @PathVariable Long boardingId
    ) {
        return adminBoardingService.deactivate(boardingId);
    }
}

