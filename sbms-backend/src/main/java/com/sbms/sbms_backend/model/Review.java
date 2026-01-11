package com.sbms.sbms_backend.model;

import com.sbms.sbms_backend.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseEntity {


    @Column(nullable = false)
    @Min(1) // Ensures rating is at least 1
    @Max(5) // Ensures rating is maximum 5
    private int rating; //

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "boarding_id", nullable = false)
    private Long boardingId;


}
