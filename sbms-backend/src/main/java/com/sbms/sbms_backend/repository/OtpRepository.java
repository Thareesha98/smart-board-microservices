package com.sbms.sbms_backend.repository;

import com.sbms.sbms_backend.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByEmailAndOtpCode(String email, String otpCode);
}
