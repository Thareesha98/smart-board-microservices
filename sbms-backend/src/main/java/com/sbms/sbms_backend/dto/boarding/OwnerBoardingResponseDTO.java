package com.sbms.sbms_backend.dto.boarding;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.sbms.sbms_backend.model.enums.BoardingType;
import com.sbms.sbms_backend.model.enums.Gender;
import com.sbms.sbms_backend.model.enums.Status;

@Data
public class OwnerBoardingResponseDTO {

    private Long id;

    private String title;
    private String description;
    private String address;

    private BigDecimal pricePerMonth;
    private BigDecimal keyMoney;

    private Gender genderType;
    private BoardingType boardingType;

    private int availableSlots;
    private Integer maxOccupants;

    private List<String> imageUrls;
    private List<String> amenities;

    private Map<String, Double> nearbyPlaces;

    private Status status;

    private boolean isBoosted;
    private LocalDateTime boostEndDate;
}
