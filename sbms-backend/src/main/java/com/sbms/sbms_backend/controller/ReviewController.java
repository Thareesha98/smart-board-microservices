package com.sbms.sbms_backend.controller;

import com.sbms.sbms_backend.dto.review.ReviewCreateDTO;
import com.sbms.sbms_backend.dto.review.ReviewResponseDTO;
import com.sbms.sbms_backend.repository.ReviewRepository;
import com.sbms.sbms_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> addReview(@RequestBody ReviewCreateDTO dto){
        return ResponseEntity.ok(reviewService.saveReview(dto));
    }

    @GetMapping("/boarding/{boardingId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviews(@PathVariable Long boardingId){
        return ResponseEntity.ok(reviewService.getReviews(boardingId));
    }

    @GetMapping("/student/{studentId}/boarding/{boardingId}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Long studentId, @PathVariable Long boardingId){
        ReviewResponseDTO dto = reviewService.getReviewByStudent(studentId,boardingId);

        if(dto != null){
            return ResponseEntity.ok(dto);
        }else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/boarding/{boardingId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long boardingId){
        return ResponseEntity.ok(reviewService.getAverageRating(boardingId));
    }

    @PutMapping("/student/{studentId}/boarding/{boardingId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Long  studentId, @PathVariable Long boardingId, @RequestBody ReviewCreateDTO dto){
        return ResponseEntity.ok(reviewService.updateReview(studentId, boardingId, dto));
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId){
        reviewRepository.deleteById(reviewId);
    }
    
}
