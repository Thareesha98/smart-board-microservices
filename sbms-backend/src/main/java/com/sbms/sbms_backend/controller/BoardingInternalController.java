package com.sbms.sbms_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_backend.dto.boarding.BoardingOwnerInfo;
import com.sbms.sbms_backend.model.Boarding;
import com.sbms.sbms_backend.repository.BoardingRepository;

@RestController
@RequestMapping("/internal/boardings")
public class BoardingInternalController {

    private final BoardingRepository boardingRepository;

    public BoardingInternalController(BoardingRepository boardingRepository) {
        this.boardingRepository = boardingRepository;
    }

    @GetMapping("/{id}/owner")
    public BoardingOwnerInfo getOwner(@PathVariable Long id) {
        Boarding boarding = boardingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boarding not found"));

        return new BoardingOwnerInfo(
                boarding.getOwner().getId(),
                boarding.getTitle()
        );
    }
}

