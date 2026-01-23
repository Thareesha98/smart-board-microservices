package com.sbms.boarding_service.controller;

import com.sbms.boarding_service.dto.boarding.BoardingOwnerInfo;
import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.model.BoardingSnapshot;
import com.sbms.boarding_service.repository.BoardingRepository;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boardings/internal")
public class BoardingInternalController {

    private final BoardingRepository boardingRepository;

    public BoardingInternalController(BoardingRepository boardingRepository) {
        this.boardingRepository = boardingRepository;
    }

    // ----------------------------------------------------
    // GET FULL BOARDING SNAPSHOT (FOR REGISTRATION)
    // ----------------------------------------------------
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

    // ----------------------------------------------------
    // GET OWNER INFO
    // ----------------------------------------------------
    @GetMapping("/{boardingId}/owner")
    public BoardingOwnerInfo getOwnerInfo(@PathVariable Long boardingId) {

        Boarding b = boardingRepository.findById(boardingId)
                .orElseThrow(() -> new RuntimeException("Boarding not found"));

        return new BoardingOwnerInfo(
                b.getOwnerId(),
                b.getTitle()
        );
    }

    // ----------------------------------------------------
    // GET BOARDING IDS BY OWNER
    // ----------------------------------------------------
    @GetMapping("/owner/{ownerId}/ids")
    public List<Long> getBoardingIdsByOwner(@PathVariable Long ownerId) {

        return boardingRepository.findAll().stream()
                .filter(b -> b.getOwnerId().equals(ownerId))
                .map(Boarding::getId)
                .toList();
    }

    // ----------------------------------------------------
    // RESERVE SLOTS (REGISTRATION APPROVE)
    // ----------------------------------------------------
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

