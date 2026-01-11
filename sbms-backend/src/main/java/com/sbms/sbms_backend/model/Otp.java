package com.sbms.sbms_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "otp_codes")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;            // email for which OTP was generated

    private String otpCode;          // 6-digit OTP

    private LocalDateTime expiresAt; // now + 10 minutes

    private boolean used = false;    // mark as used once verified
}
