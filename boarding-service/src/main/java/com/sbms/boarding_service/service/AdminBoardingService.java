package com.sbms.boarding_service.service;


import com.sbms.boarding_service.dto.boarding.OwnerBoardingResponseDTO;
import com.sbms.boarding_service.mapper.BoardingMapper;
import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.model.enums.Status;
import com.sbms.boarding_service.repository.BoardingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminBoardingService {

    private final BoardingRepository boardingRepository;

    public AdminBoardingService(BoardingRepository boardingRepository) {
        this.boardingRepository = boardingRepository;
    }

    // ----------------------------------------------------
    // VIEW ALL BOARDINGS (ANY STATUS)
    // ----------------------------------------------------
    public List<OwnerBoardingResponseDTO> getAllBoardings() {

        return boardingRepository.findAll().stream()
                .map(BoardingMapper::toOwnerResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------
    // APPROVE
    // ----------------------------------------------------
    public OwnerBoardingResponseDTO approve(Long boardingId) {

        Boarding boarding = getBoarding(boardingId);

        boarding.setStatus(Status.APPROVED);

        return BoardingMapper.toOwnerResponse(
                boardingRepository.save(boarding)
        );
    }

    // ----------------------------------------------------
    // REJECT
    // ----------------------------------------------------
    public OwnerBoardingResponseDTO reject(Long boardingId) {

        Boarding boarding = getBoarding(boardingId);

        boarding.setStatus(Status.REJECTED);

        return BoardingMapper.toOwnerResponse(
                boardingRepository.save(boarding)
        );
    }

    // ----------------------------------------------------
    // DEACTIVATE
    // ----------------------------------------------------
    public OwnerBoardingResponseDTO deactivate(Long boardingId) {

        Boarding boarding = getBoarding(boardingId);

        boarding.setStatus(Status.INACTIVE);

        return BoardingMapper.toOwnerResponse(
                boardingRepository.save(boarding)
        );
    }

    // ----------------------------------------------------
    // INTERNAL HELPER
    // ----------------------------------------------------
    private Boarding getBoarding(Long boardingId) {

        return boardingRepository.findById(boardingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Boarding not found with id: " + boardingId
                        )
                );
    }
}
