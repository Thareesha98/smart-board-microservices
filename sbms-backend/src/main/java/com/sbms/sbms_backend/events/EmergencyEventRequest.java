package com.sbms.sbms_backend.events;


import lombok.Data;

@Data
public class EmergencyEventRequest {

    private Long userId;
    private Long boardingId;
    private Double latitude;
    private Double longitude;
    private String message;
}