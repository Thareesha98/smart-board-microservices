package com.sbms.chatbot.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import com.sbms.chatbot.dto.client.AppointmentResponseDTO;
import com.sbms.chatbot.dto.client.MaintenanceResponseDTO;
import com.sbms.chatbot.dto.client.MonthlyBillResponseDTO;

@Data
@Builder
public class DynamicData {

    private String userName;
    private List<MonthlyBillResponseDTO> bills;
    private List<MaintenanceResponseDTO> maintenanceList;
    private List<AppointmentResponseDTO> appointments;
}