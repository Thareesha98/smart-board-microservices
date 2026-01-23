package com.sbms.boarding_service.dto.boarding;

import java.math.BigDecimal;
import java.util.List;

import com.sbms.boarding_service.model.enums.BoardingType;
import com.sbms.boarding_service.model.enums.Gender;
import com.sbms.boarding_service.model.enums.Status;
import lombok.Data;


@Data
public class BoardingSummaryDTO {

    private Long id;
    private String title;
    private String address;

    private BigDecimal pricePerMonth;

    private Gender genderType;          // MALE/FEMALE/BOTH
    private BoardingType boardingType;  // ROOM/ANEX
    private Status status;              // PENDING/APPROVED

    private List<String> imageUrls;

    private int availableSlots;         // mapped from available_slots
}






