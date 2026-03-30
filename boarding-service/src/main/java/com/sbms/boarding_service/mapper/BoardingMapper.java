package com.sbms.boarding_service.mapper;

import com.sbms.boarding_service.dto.boarding.*;
import com.sbms.boarding_service.dto.common.UserMinimalDTO;
import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.model.enums.Status;

import java.util.ArrayList;
import java.util.HashMap;

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

        // SAFE COLLECTION COPY
        dto.setImageUrls(
                b.getImageUrls() == null
                        ? null
                        : new ArrayList<>(b.getImageUrls())
        );

        dto.setAvailableSlots(b.getAvailable_slots());

        return dto;
    }

    // ----------------------------------------------------
    // DETAIL VIEW
    // ----------------------------------------------------
    public static BoardingDetailDTO toDetail(Boarding b , OwnerDto ownerDto) {

        BoardingDetailDTO dto = new BoardingDetailDTO();

        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setDescription(b.getDescription());
        dto.setAddress(b.getAddress());
        dto.setPricePerMonth(b.getPricePerMonth());
        dto.setKeyMoney(b.getKeyMoney());

        dto.setLatitude(b.getLatitude());
        dto.setLongitude(b.getLongitude());

        dto.setGenderType(b.getGenderType());
        dto.setBoardingType(b.getBoardingType());
        dto.setStatus(b.getStatus());

        // SAFE COLLECTION COPY
        dto.setImageUrls(
                b.getImageUrls() == null
                        ? null
                        : new ArrayList<>(b.getImageUrls())
        );

        dto.setAvailableSlots(b.getAvailable_slots());
        dto.setMaxOccupants(b.getMaxOccupants());

        // SAFE COLLECTION COPY
        dto.setAmenities(
                b.getAmenities() == null
                        ? null
                        : new ArrayList<>(b.getAmenities())
        );

        dto.setNearbyPlaces(
                b.getNearbyPlaces() == null
                        ? null
                        : new HashMap<>(b.getNearbyPlaces())
        );

        dto.setBoosted(b.getBoosted());
        dto.setBoostEndDate(b.getBoostEndDate());
        dto.setOwner(ownerDto);

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

        b.setLatitude(dto.getLatitude());
        b.setLongitude(dto.getLongitude());

        b.setGenderType(dto.getGenderType());
        b.setAvailable_slots(dto.getAvailableSlots());
        b.setMaxOccupants(dto.getMaxOccupants());
        b.setBoardingType(dto.getBoardingType());

        // SAFE COLLECTION COPY
        b.setImageUrls(
                dto.getImageUrls() == null
                        ? null
                        : new ArrayList<>(dto.getImageUrls())
        );

        b.setAmenities(
                dto.getAmenities() == null
                        ? null
                        : new ArrayList<>(dto.getAmenities())
        );

        b.setNearbyPlaces(
                dto.getNearbyPlaces() == null
                        ? null
                        : new HashMap<>(dto.getNearbyPlaces())
        );

        // SYSTEM RULE
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

        dto.setLatitude(b.getLatitude());
        dto.setLongitude(b.getLongitude());

        dto.setGenderType(b.getGenderType());
        dto.setBoardingType(b.getBoardingType());

        dto.setAvailableSlots(b.getAvailable_slots());
        dto.setMaxOccupants(b.getMaxOccupants());

        dto.setImageUrls(
                b.getImageUrls() == null
                        ? null
                        : new ArrayList<>(b.getImageUrls())
        );

        dto.setAmenities(
                b.getAmenities() == null
                        ? null
                        : new ArrayList<>(b.getAmenities())
        );

        dto.setNearbyPlaces(
                b.getNearbyPlaces() == null
                        ? null
                        : new HashMap<>(b.getNearbyPlaces())
        );

        dto.setStatus(b.getStatus());
        dto.setBoosted(b.getBoosted());
        dto.setBoostEndDate(b.getBoostEndDate());

        return dto;
    }
}