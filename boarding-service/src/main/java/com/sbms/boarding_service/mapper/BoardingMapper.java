package com.sbms.boarding_service.mapper;

import com.sbms.boarding_service.dto.boarding.BoardingCreateDTO;
import com.sbms.boarding_service.dto.boarding.BoardingDetailDTO;
import com.sbms.boarding_service.dto.boarding.BoardingSummaryDTO;
import com.sbms.boarding_service.dto.boarding.OwnerBoardingResponseDTO;
import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.model.enums.Status;

public class BoardingMapper {

    // ----------------------------------------------------
    // SUMMARY (LIST / SEARCH VIEW)
    // ----------------------------------------------------
    public static BoardingSummaryDTO toSummary(Boarding b) {
        BoardingSummaryDTO dto = new BoardingSummaryDTO();

        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setAddress(b.getAddress());
        dto.setPricePerMonth(b.getPricePerMonth());

        dto.setGenderType(b.getGenderType());
        dto.setBoardingType(b.getBoardingType());
        dto.setStatus(b.getStatus());

        dto.setImageUrls(b.getImageUrls());
        dto.setAvailableSlots(b.getAvailable_slots());

        return dto;
    }

    // ----------------------------------------------------
    // DETAIL VIEW
    // ----------------------------------------------------
    public static BoardingDetailDTO toDetail(Boarding b) {
        BoardingDetailDTO dto = new BoardingDetailDTO();

        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setDescription(b.getDescription());
        dto.setAddress(b.getAddress());
        dto.setPricePerMonth(b.getPricePerMonth());
        dto.setKeyMoney(b.getKeyMoney());

        dto.setGenderType(b.getGenderType());
        dto.setBoardingType(b.getBoardingType());
        dto.setStatus(b.getStatus());

        dto.setImageUrls(b.getImageUrls());
        dto.setAvailableSlots(b.getAvailable_slots());
        dto.setMaxOccupants(b.getMaxOccupants());

        dto.setAmenities(b.getAmenities());
        dto.setNearbyPlaces(b.getNearbyPlaces());

        dto.setBoosted(b.getBoosted());
        dto.setBoostEndDate(b.getBoostEndDate());

        return dto;
    }

    // ----------------------------------------------------
    // CREATE → ENTITY
    // ----------------------------------------------------
    public static Boarding toEntityFromCreate(BoardingCreateDTO dto) {
        Boarding b = new Boarding();

        b.setTitle(dto.getTitle());
        b.setDescription(dto.getDescription());
        b.setAddress(dto.getAddress());
        b.setPricePerMonth(dto.getPricePerMonth());
        b.setKeyMoney(dto.getKeyMoney());
        b.setGenderType(dto.getGenderType());
        b.setAvailable_slots(dto.getAvailableSlots());
        b.setMaxOccupants(dto.getMaxOccupants());
        b.setBoardingType(dto.getBoardingType());
        b.setImageUrls(dto.getImageUrls());
        b.setAmenities(dto.getAmenities());
        b.setNearbyPlaces(dto.getNearbyPlaces());

        // 🔐 SYSTEM RULE: new boarding = PENDING
        b.setStatus(Status.PENDING);

        return b;
    }

    // ----------------------------------------------------
    // OWNER RESPONSE
    // ----------------------------------------------------
    public static OwnerBoardingResponseDTO toOwnerResponse(Boarding b) {
        OwnerBoardingResponseDTO dto = new OwnerBoardingResponseDTO();

        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setDescription(b.getDescription());
        dto.setAddress(b.getAddress());
        dto.setPricePerMonth(b.getPricePerMonth());
        dto.setKeyMoney(b.getKeyMoney());

        dto.setGenderType(b.getGenderType());
        dto.setBoardingType(b.getBoardingType());
        dto.setAvailableSlots(b.getAvailable_slots());
        dto.setMaxOccupants(b.getMaxOccupants());

        dto.setImageUrls(b.getImageUrls());
        dto.setAmenities(b.getAmenities());
        dto.setNearbyPlaces(b.getNearbyPlaces());

        dto.setStatus(b.getStatus());
        dto.setBoosted(b.getBoosted());
        dto.setBoostEndDate(b.getBoostEndDate());

        return dto;
    }
}
