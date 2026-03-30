package com.sbms.sbms_user_service.dto.technical;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TechnicianReviewDTO {

    private Long id;

    private String ownerName;

    private String ownerProfileImageUrl;

    private int rating;

    private String comment;

    private LocalDate date;
}