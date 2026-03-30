package com.sbms.chatbot.dto.client;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.sbms.chatbot.model.enums.BillDueStatus;
import com.sbms.chatbot.model.enums.MonthlyBillStatus;

@Data
public class MonthlyBillResponseDTO {

    private Long id;

    private Long studentId;
    private String studentName;
    
    private LocalDate dueDate;
    private int dueInDays;
    private BillDueStatus dueStatus;
    
    private long ownerId;


    private Long boardingId;
    private String boardingTitle;

    private String month;

    private BigDecimal boardingFee;
    private BigDecimal electricityFee;
    private BigDecimal waterFee;
    private BigDecimal totalAmount;

    private MonthlyBillStatus status;
}
