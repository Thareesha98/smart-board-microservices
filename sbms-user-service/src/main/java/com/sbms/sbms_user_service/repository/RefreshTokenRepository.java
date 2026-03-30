package com.sbms.sbms_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_user_service.model.RefreshToken;
import com.sbms.sbms_user_service.model.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    void deleteByUser(User user);
}
