package com.sbms.sbms_backend.mapper;

import com.sbms.sbms_backend.dto.user.*;
import com.sbms.sbms_backend.model.User;
import com.sbms.sbms_backend.model.enums.UserRole;

public class UserMapper {

    // ---------------------------------------------------------
    // REGISTER DTO → USER ENTITY
    // ---------------------------------------------------------
    public static User toEntity(UserRegisterDTO dto) {
        if (dto == null) return null;

        User user = new User();

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());  // will be encoded in service
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setGender(dto.getGender());

        user.setRole(dto.getRole());

        // OWNER fields
        if (dto.getRole() == UserRole.OWNER) {
            user.setNicNumber(dto.getNicNumber());
            user.setAccNo(dto.getAccNo());

            // Owner initially not verified
            user.setVerifiedOwner(false);
            user.setSubscription_id(0);
        }

        // STUDENT fields
        if (dto.getRole() == UserRole.STUDENT) {
            user.setStudentUniversity(dto.getStudentUniversity());
        }

        return user;
    }

    // ---------------------------------------------------------
    // USER ENTITY → USER RESPONSE DTO (general purpose)
    // ---------------------------------------------------------
    public static UserResponseDTO toUserResponse(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setAddress(user.getAddress());
        dto.setGender(user.getGender());
        dto.setRole(user.getRole());

        // OWNER fields
        if (user.getRole() == UserRole.OWNER) {
            dto.setVerifiedOwner(user.isVerifiedOwner());
            dto.setSubscription_id(user.getSubscription_id());
            dto.setAccNo(user.getAccNo());
        }

        // STUDENT fields
        if (user.getRole() == UserRole.STUDENT) {
            dto.setStudentUniversity(user.getStudentUniversity());
        }

        return dto;
    }

    // ---------------------------------------------------------
    // USER ENTITY → OWNER PROFILE DTO
    // ---------------------------------------------------------
    public static OwnerProfileDTO toOwnerProfile(User user) {
        if (user == null) return null;

        OwnerProfileDTO dto = new OwnerProfileDTO();

        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());

        dto.setAddress(user.getAddress());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setNicNumber(user.getNicNumber());
        dto.setVerifiedOwner(user.isVerifiedOwner());

        dto.setSubscription_id(user.getSubscription_id());
        dto.setAccNo(user.getAccNo());

        if (user.getBoardings() != null) {
            dto.setTotalBoardings(user.getBoardings().size());
        } else {
            dto.setTotalBoardings(0);
        }

        return dto;
    }

    // ---------------------------------------------------------
    // USER ENTITY → ADMIN USER DTO
    // For admin user list dashboard
    // ---------------------------------------------------------
    public static AdminUserDTO toAdminUser(User user) {
        if (user == null) return null;

        AdminUserDTO dto = new AdminUserDTO();

        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());

        dto.setVerifiedOwner(user.isVerifiedOwner());
        dto.setSubscription_id(user.getSubscription_id());

        return dto;
    }
}
