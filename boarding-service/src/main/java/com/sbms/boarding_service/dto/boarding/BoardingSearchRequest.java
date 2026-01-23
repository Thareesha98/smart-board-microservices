package com.sbms.boarding_service.dto.boarding;


import java.math.BigDecimal;

import com.sbms.boarding_service.model.enums.BoardingType;
import com.sbms.boarding_service.model.enums.Gender;

import lombok.Data;

@Data
public class BoardingSearchRequest {

    // for simple search – student experience
    private String addressKeyword;        // optional: part of address/title

    private Gender genderType;            // optional
    private BoardingType boardingType;    // optional

    private BigDecimal minPrice;          // optional
    private BigDecimal maxPrice;  
    
    
    private BigDecimal minKeyMoney;          // optional
    private BigDecimal maxKeyMoney;  // optional
    
    

    // pagination
    private int page = 0;
    private int size = 10;
}
