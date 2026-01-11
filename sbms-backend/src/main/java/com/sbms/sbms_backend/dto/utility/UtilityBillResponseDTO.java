package com.sbms.sbms_backend.dto.utility;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class UtilityBillResponseDTO {

    private Long id;

    private Long boardingId;
    private String boardingTitle;

    private String month;

    private BigDecimal electricityAmount;
    private BigDecimal waterAmount;

    private BigDecimal totalUtilityCost;
}
