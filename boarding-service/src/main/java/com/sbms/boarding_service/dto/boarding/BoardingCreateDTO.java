package com.sbms.boarding_service.dto.boarding;

import com.sbms.boarding_service.model.enums.BoardingType;
import com.sbms.boarding_service.model.enums.Gender;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class BoardingCreateDTO {

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
    private Boolean boosted = false;

    private Map<String, Double> nearbyPlaces;
}
