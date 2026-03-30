package com.sbms.sbms_user_service.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sbms.sbms_user_service.enums.OtpPurpose;
import com.sbms.sbms_user_service.model.Otp;
import com.sbms.sbms_user_service.repository.OtpRepository;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    // ---------------------------------------------------------
    // PUBLIC API
    // ---------------------------------------------------------

    public Otp createRegistrationOtp(String email) {
        return createOtp(email, OtpPurpose.REGISTRATION);
    }

    public Otp createPasswordResetOtp(String email) {
        return createOtp(email, OtpPurpose.PASSWORD_RESET);
    }

    public boolean validateOtp(String email, String otpCode, OtpPurpose purpose) {

        Otp otp = otpRepository
                .findByEmailAndOtpCodeAndPurpose(
                        email.trim(),
                        otpCode.trim(),
                        purpose
                )
                .orElse(null);

        if (otp == null) return false;
        if (otp.isUsed()) return false;
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) return false;

        otp.setUsed(true);
        otpRepository.save(otp);

        return true;
    }

    // ---------------------------------------------------------
    // INTERNAL
    // ---------------------------------------------------------

    private Otp createOtp(String email, OtpPurpose purpose) {

        Otp otp = otpRepository
                .findByEmailAndPurpose(email, purpose)
                .orElse(new Otp());

        if (otp.getId() != null &&
            !otp.isUsed() &&
            otp.getExpiresAt().isAfter(LocalDateTime.now())) {
            return otp; // reuse valid OTP
        }

        otp.setEmail(email);
        otp.setPurpose(purpose);
        otp.setOtpCode(generateOtp());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);

        return otpRepository.save(otp);
    }

    private String generateOtp() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}
