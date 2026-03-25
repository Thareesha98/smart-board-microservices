package com.sbms.boarding_service.dto.boarding;

import lombok.Data;

@Data
public class OwnerDto {
    private Long id;
    private String name;
    private String contact;
    private String email;
    private String image;
}