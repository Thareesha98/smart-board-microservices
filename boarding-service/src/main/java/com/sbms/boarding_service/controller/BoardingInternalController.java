package com.sbms.boarding_service.controller;

import com.sbms.boarding_service.dto.boarding.BoardingOwnerInfo;
import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.model.BoardingSnapshot;
import com.sbms.boarding_service.repository.BoardingRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boardings/internal")
@Tag(
	    name = "Internal Boardings",
	    description = "Internal APIs used by other microservices (NOT for frontend use)"
	)
public class BoardingInternalController {

    private final BoardingRepository boardingRepository;

    public BoardingInternalController(BoardingRepository boardingRepository) {
        this.boardingRepository = boardingRepository;
    }

    @Operation(
            summary = "Get boarding snapshot",
            description = "Returns a lightweight snapshot of boarding details "
                        + "used during registration and billing processes"
        )
    @GetMapping("/{boardingId}")
    public BoardingSnapshot getBoarding(@PathVariable Long boardingId) {

        Boarding b = boardingRepository.findById(boardingId)
                .orElseThrow(() -> new RuntimeException("Boarding not found"));

        return new BoardingSnapshot(
                b.getId(),
                b.getOwnerId(),
                b.getTitle(),
                b.getPricePerMonth(),
                b.getKeyMoney(),
                b.getAvailable_slots()
        );
    }

   
    
    
    @Operation(
            summary = "Get boarding owner info",
            description = "Returns owner-related information for a boarding"
        )
    @GetMapping("/{boardingId}/owner")
    public BoardingOwnerInfo getOwnerInfo(@PathVariable Long boardingId) {

        Boarding b = boardingRepository.findById(boardingId)
                .orElseThrow(() -> new RuntimeException("Boarding not found"));

        return new BoardingOwnerInfo(
                b.getOwnerId(),
                b.getTitle()
        );
    }

    
    
    @Operation(
            summary = "Get boarding IDs by owner",
            description = "Used by admin and billing services to fetch boardings of an owner"
        )
    @GetMapping("/owner/{ownerId}/ids")
    public List<Long> getBoardingIdsByOwner(@PathVariable Long ownerId) {

        return boardingRepository.findAll().stream()
                .filter(b -> b.getOwnerId().equals(ownerId))
                .map(Boarding::getId)
                .toList();
    }

   
    
    @Operation(
            summary = "Reserve boarding slots",
            description = "Reduces available slots after a successful registration approval"
        )
    @PostMapping("/{boardingId}/reserve-slots")
    public void reserveSlots(
            @PathVariable Long boardingId,
            @RequestParam int count
    ) {
        Boarding b = boardingRepository.findById(boardingId)
                .orElseThrow(() -> new RuntimeException("Boarding not found"));

        if (b.getAvailable_slots() < count) {
            throw new RuntimeException("Not enough slots");
        }

        b.setAvailable_slots(b.getAvailable_slots() - count);
        boardingRepository.save(b);
    }
}

