package com.sbms.boarding_service.service;


import org.springframework.stereotype.Service;

import com.sbms.boarding_service.dto.boarding.BoardingCreateDTO;
import com.sbms.boarding_service.dto.boarding.BoardingUpdateDTO;
import com.sbms.boarding_service.dto.boarding.OwnerBoardingResponseDTO;
import com.sbms.boarding_service.mapper.BoardingMapper;
import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.repository.BoardingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerBoardingService {

    private final BoardingRepository boardingRepository;

    public OwnerBoardingService(BoardingRepository boardingRepository) {
        this.boardingRepository = boardingRepository;
    }

    public OwnerBoardingResponseDTO create(Long ownerId, BoardingCreateDTO dto) {

        Boarding b = BoardingMapper.toEntityFromCreate(dto);
        b.setOwnerId(ownerId);

        return BoardingMapper.toOwnerResponse(boardingRepository.save(b));
    }

    public OwnerBoardingResponseDTO update(Long ownerId, Long boardingId, BoardingUpdateDTO dto) {

        Boarding b = getOwnedBoarding(ownerId, boardingId);

        b.setTitle(dto.getTitle());
        b.setDescription(dto.getDescription());
        b.setAddress(dto.getAddress());
        b.setPricePerMonth(dto.getPricePerMonth());
        b.setKeyMoney(dto.getKeyMoney());
        b.setGenderType(dto.getGenderType());
        b.setAvailable_slots(dto.getAvailableSlots());
        b.setMaxOccupants(dto.getMaxOccupants());
        b.setBoardingType(dto.getBoardingType());
        b.setAmenities(dto.getAmenities());
        b.setImageUrls(dto.getImageUrls());
        b.setNearbyPlaces(dto.getNearbyPlaces());

        return BoardingMapper.toOwnerResponse(boardingRepository.save(b));
    }

    public void delete(Long ownerId, Long boardingId) {
        boardingRepository.delete(getOwnedBoarding(ownerId, boardingId));
    }

    public List<OwnerBoardingResponseDTO> getAllByOwner(Long ownerId) {
        return boardingRepository.findAll().stream()
                .filter(b -> b.getOwnerId().equals(ownerId))
                .map(BoardingMapper::toOwnerResponse)
                .collect(Collectors.toList());
    }

    public OwnerBoardingResponseDTO boost(Long ownerId, Long boardingId, int days) {

        Boarding b = getOwnedBoarding(ownerId, boardingId);
        b.setBoosted(true);
        b.setBoostEndDate(LocalDateTime.now().plusDays(days));

        return BoardingMapper.toOwnerResponse(boardingRepository.save(b));
    }

    private Boarding getOwnedBoarding(Long ownerId, Long boardingId) {
        Boarding b = boardingRepository.findById(boardingId)
                .orElseThrow(() -> new RuntimeException("Boarding not found"));

        if (!b.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Not allowed");
        }
        return b;
    }
}
