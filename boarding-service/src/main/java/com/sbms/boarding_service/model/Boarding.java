package com.sbms.boarding_service.model;

import com.sbms.boarding_service.common.BaseEntity;
import com.sbms.boarding_service.model.enums.BoardingType;
import com.sbms.boarding_service.model.enums.Gender;
import com.sbms.boarding_service.model.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "boardings")
public class Boarding extends BaseEntity {

    // ----------------------------------------------------
    // OWNER REFERENCE (NO USER ENTITY)
    // ----------------------------------------------------
    @Column(nullable = false)
    private Long ownerId;

    // ----------------------------------------------------
    // BASIC DETAILS
    // ----------------------------------------------------
    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private BigDecimal pricePerMonth;

    private BigDecimal keyMoney;

    // ----------------------------------------------------
    // MEDIA & FACILITIES (EAGER to avoid LazyInitException)
    // ----------------------------------------------------
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> imageUrls;
    
    
    

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "boarding_nearby_places",
            joinColumns = @JoinColumn(name = "boarding_id")
    )
    @MapKeyColumn(name = "place_name")
    @Column(name = "distance")
    private Map<String, Double> nearbyPlaces = new HashMap<>();



    private Double latitude;
    private Double longitude;
    
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "boarding_amenities",
            joinColumns = @JoinColumn(name = "boarding_id")
    )
    @Column(name = "amenity")
    private List<String> amenities;

    // ----------------------------------------------------
    // RULES & CAPACITY
    // ----------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender genderType;

    @Column(nullable = false)
    private int available_slots;

    @Column(nullable = false)
    private Integer maxOccupants;

    @Enumerated(EnumType.STRING)
    private BoardingType boardingType;

    // ----------------------------------------------------
    // STATUS & VISIBILITY
    // ----------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // ----------------------------------------------------
    // BOOSTING
    // ----------------------------------------------------
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean boosted = false;

    private LocalDateTime boostEndDate;
}
