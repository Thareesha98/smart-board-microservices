package com.sbms.sbms_backend.service;

import com.sbms.sbms_backend.dto.review.ReviewCreateDTO;
import com.sbms.sbms_backend.dto.review.ReviewResponseDTO;
import com.sbms.sbms_backend.mapper.ReviewMapper;
import com.sbms.sbms_backend.model.Review;
import com.sbms.sbms_backend.repository.ReviewRepository;
import com.sbms.sbms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponseDTO saveReview(ReviewCreateDTO dto){

        //Prevent duplicate reviews
        if(reviewRepository.existsByStudentIdAndBoardingId(dto.getStudentId(),dto.getBoardingId())){
            throw new IllegalStateException("You have already reviewed this boarding house.");
        }

        //Map DTO to Entity
        Review review = reviewMapper.toEntity(dto);

        //Set Student and Boarding to review
        review.setStudent(userRepository.getReferenceById(dto.getStudentId()));
        review.setBoardingId(dto.getBoardingId());

        return reviewMapper.toResponseDto(reviewRepository.save(review));

    }

    @Transactional
    public ReviewResponseDTO updateReview(Long studentId,Long boardingId,ReviewCreateDTO dto){

        //Find exists or throw error
        Review review = reviewRepository.findByStudentIdAndBoardingId(studentId,boardingId)
                .orElseThrow(()->new IllegalStateException("Review Not Found"));

        //Update fields
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        return reviewMapper.toResponseDto(reviewRepository.save(review));
    }

    public List<ReviewResponseDTO> getReviews(Long boardingId){

        return reviewRepository.findByBoardingIdOrderByCreatedAtDesc(boardingId)
                .stream()
                .map(reviewMapper::toResponseDto)
                .collect(Collectors.toList());

    }

    public ReviewResponseDTO getReviewByStudent(Long studentId,Long boardingId){
        return reviewRepository.findByStudentIdAndBoardingId(studentId,boardingId)
                .map(reviewMapper::toResponseDto)
                .orElse(null);

    }

    public Double getAverageRating(Long boardingId){
        Double avg = reviewRepository.getAverageRatingForBoarding(boardingId);
        return (avg != null) ? avg : 0.0;

    }



}
