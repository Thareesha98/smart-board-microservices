package com.sbms.sbms_user_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sbms.sbms_monolith.dto.admin.AdminUserResponseDTO;
import com.sbms.sbms_monolith.dto.admin.UserVerificationDTO;
import com.sbms.sbms_user_service.enums.UserRole;
import com.sbms.sbms_user_service.model.User;
import com.sbms.sbms_user_service.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AdminUserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(AdminUserResponseDTO::fromEntity)
                .toList();
    }

    public void verifyOwner(Long userId, UserVerificationDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != UserRole.OWNER) {
            throw new RuntimeException("User is not an owner");
        }

        user.setVerifiedOwner(dto.isApproved());

        // Optional: persist admin note later
    }
}
