package com.sbms.sbms_backend.dto.boarding;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.sbms.sbms_backend.model.enums.BoardingType;
import com.sbms.sbms_backend.model.enums.Gender;

@Data
public class BoardingUpdateDTO {

    private String title;
    private String description;
    private String address;

    private BigDecimal pricePerMonth;
    private BigDecimal keyMoney;

    private Gender genderType;

    private int availableSlots;

    private Integer maxOccupants;

    private BoardingType boardingType;

    private List<String> imageUrls;

    private List<String> amenities;

    private Map<String, Double> nearbyPlaces;
}
