package com.sbms.sbms_backend.dto.review;

import lombok.Data;

@Data
public class ReviewCreateDTO {
    private int rating;
    private String comment;
    private Long studentId;
    private Long boardingId;
}
