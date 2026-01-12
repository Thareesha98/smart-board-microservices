package com.sbms.sbms_backend.service;

import com.sbms.sbms_backend.model.RefreshToken;
import com.sbms.sbms_backend.model.User;
import com.sbms.sbms_backend.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private long refreshTokenDurationMs = 7L * 24 * 60 * 60 * 1000; // 7 days

    // Create or replace a refresh token for a user
    public RefreshToken createRefreshToken(User user) {

        // Optionally delete old refresh token
        refreshTokenRepository.findByUser(user)
                .ifPresent(existing -> refreshTokenRepository.delete(existing));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }
        return token;
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }
}
