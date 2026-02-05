package com.sbms.sbms_backend.dto.boarding;

import java.math.BigDecimal;
import java.util.List;

import com.sbms.sbms_backend.model.enums.BoardingType;
import com.sbms.sbms_backend.model.enums.Gender;
import com.sbms.sbms_backend.model.enums.Status;

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

    private double rating;
    private int reviewCount;
}






