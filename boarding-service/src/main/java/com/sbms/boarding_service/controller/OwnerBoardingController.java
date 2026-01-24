package com.sbms.boarding_service.controller;

import com.sbms.boarding_service.dto.boarding.*;
import com.sbms.boarding_service.service.OwnerBoardingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/boardings/owner")
@CrossOrigin
@Tag(
	    name = "Owner Boardings",
	    description = "APIs for boarding owners to manage their boardings"
	)
	@SecurityRequirement(name = "BearerAuth")
public class OwnerBoardingController {

    private final OwnerBoardingService ownerBoardingService;

    public OwnerBoardingController(OwnerBoardingService ownerBoardingService) {
        this.ownerBoardingService = ownerBoardingService;
    }


    @Operation(
            summary = "Create boarding",
            description = "Allows a boarding owner to create a new boarding advertisement"
        )
    @PostMapping
    public OwnerBoardingResponseDTO create(
            @RequestHeader("X-User-Id") Long ownerId,
            @RequestBody BoardingCreateDTO dto
    ) {
        return ownerBoardingService.create(ownerId, dto);
    }

    
    
    @Operation(
            summary = "Update boarding",
            description = "Allows a boarding owner to update an existing boarding"
        )
    @PutMapping("/{boardingId}")
    public OwnerBoardingResponseDTO update(
            @RequestHeader("X-User-Id") Long ownerId,
            @PathVariable Long boardingId,
            @RequestBody BoardingUpdateDTO dto
    ) {
        return ownerBoardingService.update(ownerId, boardingId, dto);
    }

   
    
    @Operation(
            summary = "Delete boarding",
            description = "Deletes a boarding owned by the authenticated owner"
        )
    @DeleteMapping("/{boardingId}")
    public String delete(
            @RequestHeader("X-User-Id") Long ownerId,
            @PathVariable Long boardingId
    ) {
        ownerBoardingService.delete(ownerId, boardingId);
        return "Boarding deleted successfully.";
    }

    
    
    @Operation(
            summary = "Get owner boardings",
            description = "Returns all boardings created by the authenticated owner"
        )
    @GetMapping
    public List<OwnerBoardingResponseDTO> getAll(
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        return ownerBoardingService.getAllByOwner(ownerId);
    }

    @Operation(
            summary = "Boost boarding",
            description = "Boosts a boarding advertisement for better visibility for given days"
        )
    @PostMapping("/{boardingId}/boost")
    public OwnerBoardingResponseDTO boost(
            @RequestHeader("X-User-Id") Long ownerId,
            @PathVariable Long boardingId,
            @RequestParam int days
    ) {
        return ownerBoardingService.boost(ownerId, boardingId, days);
    }
}
