package com.sbms.boarding_service.service;

import com.sbms.boarding_service.dto.boarding.OwnerBoardingResponseDTO;
import com.sbms.boarding_service.event.BoardingEventPublisher;
import com.sbms.boarding_service.mapper.BoardingMapper;
import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.model.enums.Status;
import com.sbms.boarding_service.repository.BoardingRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminBoardingService {

    private final BoardingRepository boardingRepository;
    private final BoardingEventPublisher eventPublisher;

    public AdminBoardingService(BoardingRepository boardingRepository,
                                BoardingEventPublisher eventPublisher) {
        this.boardingRepository = boardingRepository;
        this.eventPublisher = eventPublisher;
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

        Boarding saved = boardingRepository.save(boarding);

        publishEvent("boarding.approved", saved);

        return BoardingMapper.toOwnerResponse(saved);
    }

    // ----------------------------------------------------
    // REJECT
    // ----------------------------------------------------
    public OwnerBoardingResponseDTO reject(Long boardingId) {

        Boarding boarding = getBoarding(boardingId);
        boarding.setStatus(Status.REJECTED);

        Boarding saved = boardingRepository.save(boarding);

        publishEvent("boarding.rejected", saved);

        return BoardingMapper.toOwnerResponse(saved);
    }

    // ----------------------------------------------------
    // DEACTIVATE
    // ----------------------------------------------------
    public OwnerBoardingResponseDTO deactivate(Long boardingId) {

        Boarding boarding = getBoarding(boardingId);
        boarding.setStatus(Status.INACTIVE);

        Boarding saved = boardingRepository.save(boarding);

        publishEvent("boarding.deactivated", saved);

        return BoardingMapper.toOwnerResponse(saved);
    }

    // ----------------------------------------------------
    // EVENT PUBLISHING (CENTRALIZED)
    // ----------------------------------------------------
    private void publishEvent(String eventType, Boarding boarding) {

        Map<String, Object> data = new HashMap<>();
        data.put("boardingId", boarding.getId());
        data.put("status", boarding.getStatus().name());
        data.put("title", boarding.getTitle());
        data.put("ownerId", boarding.getOwnerId());

        eventPublisher.publish(
                eventType,
                boarding.getOwnerId(),     // target user (owner)
                boarding.getId(),
                data
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
