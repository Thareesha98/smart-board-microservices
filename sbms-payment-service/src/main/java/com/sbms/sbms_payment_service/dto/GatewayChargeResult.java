package com.sbms.sbms_payment_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class GatewayChargeResult {

    private boolean success;
    private String gatewayRef;
    private String message;
}