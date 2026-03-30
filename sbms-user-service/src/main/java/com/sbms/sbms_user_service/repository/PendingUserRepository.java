package com.sbms.sbms_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_user_service.model.PendingUser;

import java.util.Optional;

public interface PendingUserRepository extends JpaRepository<PendingUser, Long> {

    Optional<PendingUser> findByEmail(String email);
}
