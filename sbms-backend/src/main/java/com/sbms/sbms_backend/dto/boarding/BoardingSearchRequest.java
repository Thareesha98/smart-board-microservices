package com.sbms.sbms_backend.dto.boarding;


import java.math.BigDecimal;

import com.sbms.sbms_backend.model.enums.BoardingType;
import com.sbms.sbms_backend.model.enums.Gender;

import lombok.Data;

@Data
public class BoardingSearchRequest {

    private String addressKeyword;        
    private Gender genderType;            
    private BoardingType boardingType;   

    private BigDecimal minPrice;         
    private BigDecimal maxPrice;  
    
    
    private BigDecimal minKeyMoney;          
    private BigDecimal maxKeyMoney;  
    
    

    private int page = 0;
    private int size = 10;
}
