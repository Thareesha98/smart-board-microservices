package com.sbms.boarding_service.controller;

import com.sbms.boarding_service.dto.boarding.*;
import com.sbms.boarding_service.service.OwnerBoardingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/boardings/owner")
@CrossOrigin
public class OwnerBoardingController {

    private final OwnerBoardingService ownerBoardingService;

    public OwnerBoardingController(OwnerBoardingService ownerBoardingService) {
        this.ownerBoardingService = ownerBoardingService;
    }

    // ----------------------------------------------------
    // CREATE BOARDING
    // ----------------------------------------------------
    @PostMapping
    public OwnerBoardingResponseDTO create(
            @RequestHeader("X-User-Id") Long ownerId,
            @RequestBody BoardingCreateDTO dto
    ) {
        return ownerBoardingService.create(ownerId, dto);
    }

    // ----------------------------------------------------
    // UPDATE BOARDING
    // ----------------------------------------------------
    @PutMapping("/{boardingId}")
    public OwnerBoardingResponseDTO update(
            @RequestHeader("X-User-Id") Long ownerId,
            @PathVariable Long boardingId,
            @RequestBody BoardingUpdateDTO dto
    ) {
        return ownerBoardingService.update(ownerId, boardingId, dto);
    }

    // ----------------------------------------------------
    // DELETE BOARDING
    // ----------------------------------------------------
    @DeleteMapping("/{boardingId}")
    public String delete(
            @RequestHeader("X-User-Id") Long ownerId,
            @PathVariable Long boardingId
    ) {
        ownerBoardingService.delete(ownerId, boardingId);
        return "Boarding deleted successfully.";
    }

    // ----------------------------------------------------
    // GET ALL OWNER BOARDINGS
    // ----------------------------------------------------
    @GetMapping
    public List<OwnerBoardingResponseDTO> getAll(
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        return ownerBoardingService.getAllByOwner(ownerId);
    }

    // ----------------------------------------------------
    // BOOST BOARDING
    // ----------------------------------------------------
    @PostMapping("/{boardingId}/boost")
    public OwnerBoardingResponseDTO boost(
            @RequestHeader("X-User-Id") Long ownerId,
            @PathVariable Long boardingId,
            @RequestParam int days
    ) {
        return ownerBoardingService.boost(ownerId, boardingId, days);
    }
}
