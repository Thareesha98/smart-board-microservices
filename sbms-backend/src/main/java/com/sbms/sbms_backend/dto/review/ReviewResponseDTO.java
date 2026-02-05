package com.sbms.sbms_backend.dto.review;

import lombok.Data;

@Data
public class ReviewResponseDTO {
    private Long id;
    private int rating;
    private String comment;
    private String createdAt;

    private String studentName;
    private String studentProfileImage;
}
