package com.sbms.sbms_backend.mapper;

import com.sbms.sbms_backend.dto.review.ReviewCreateDTO;
import com.sbms.sbms_backend.dto.review.ReviewResponseDTO;
import com.sbms.sbms_backend.dto.user.UserSnapshotDTO;
import com.sbms.sbms_backend.model.Review;
import com.sbms.sbms_backend.client.UserClient;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ReviewMapper {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final UserClient userClient;

    public ReviewMapper(UserClient userClient) {
        this.userClient = userClient;
    }

    // ---------------------------------------------------------
    // CREATE DTO → ENTITY
    // ---------------------------------------------------------
    public Review toEntity(ReviewCreateDTO dto, Long studentId) {

        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setStudentId(studentId);   // ✅ ID only
        review.setBoardingId(dto.getBoardingId());

        return review;
    }

    // ---------------------------------------------------------
    // ENTITY → RESPONSE DTO
    // ---------------------------------------------------------
    public ReviewResponseDTO toResponseDto(Review review) {

        ReviewResponseDTO dto = new ReviewResponseDTO();

        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());

        if (review.getCreatedAt() != null) {
            dto.setCreatedAt(
                    review.getCreatedAt().format(DATE_FORMATTER)
            );
        }

        // ✅ Fetch user snapshot from user-service
        UserSnapshotDTO student =
                userClient.getUserSnapshot(review.getStudentId());

        dto.setStudentName(student.getFullName());
        dto.setStudentProfileImage(student.getProfileImageUrl());

        return dto;
    }
}
