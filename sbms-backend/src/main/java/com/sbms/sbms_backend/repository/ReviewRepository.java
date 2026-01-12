package com.sbms.sbms_backend.repository;

import com.sbms.sbms_backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Get all reviews for a specific boarding, newest first
    List<Review> findByBoardingIdOrderByCreatedAtDesc(Long boardingId);

    // Find a specific review by a student for a specific boarding
    Optional<Review> findByStudentIdAndBoardingId(Long studentId, Long boardingId);

    // Calculate the average rating for a boarding
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.boardingId = :boardingId")
    Double getAverageRatingForBoarding(Long boardingId);

    // Check if a student has already reviewed this boarding
    boolean existsByStudentIdAndBoardingId(Long studentId, Long boardingId);
}
