package com.sbms.sbms_user_service.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sbms.sbms_user_service.model.RefreshToken;
import com.sbms.sbms_user_service.model.User;
import com.sbms.sbms_user_service.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final long refreshTokenDurationMs =
            7L * 24 * 60 * 60 * 1000;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.refreshTokenRepository = repo;
    }

    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(
                Instant.now().plusMillis(refreshTokenDurationMs)
        );

        return refreshTokenRepository.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }
}
