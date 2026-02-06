package com.sbms.boarding_service.dto.boarding;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;




import lombok.Data;

import com.sbms.boarding_service.model.enums.*;



@Data
public class BoardingDetailDTO {

    private Long id;

    private String title;
    private String description;
    private String address;

    private BigDecimal pricePerMonth;
    private BigDecimal keyMoney;
    
    private Double latitude;
    private Double longitude;
    
    private Gender genderType;
    private BoardingType boardingType;
    private Status status;

    private List<String> imageUrls;
    private int availableSlots;
    private Integer maxOccupants;

    private List<String> amenities;
    private Map<String, Double> nearbyPlaces;

    private boolean boosted;             // from isBosted
    private LocalDateTime boostEndDate;
}
