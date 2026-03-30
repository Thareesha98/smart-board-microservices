package com.sbms.sbms_user_service.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.sbms.sbms_user_service.dto.user.AdminUserDTO;
import com.sbms.sbms_user_service.dto.user.OwnerProfileDTO;
import com.sbms.sbms_user_service.dto.user.UserRegisterDTO;
import com.sbms.sbms_user_service.dto.user.UserResponseDTO;
import com.sbms.sbms_user_service.enums.UserRole;
import com.sbms.sbms_user_service.model.User;

public class UserMapper {
	public static User toEntity(UserRegisterDTO dto) {
        if (dto == null) return null;

        User user = new User();

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); 
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setGender(dto.getGender());

        user.setRole(dto.getRole());

        if (dto.getRole() == UserRole.OWNER) {
            user.setNicNumber(dto.getNicNumber());
            user.setAccNo(dto.getAccNo());

            user.setVerifiedOwner(false);
            user.setSubscription_id(0);
        }

        if (dto.getRole() == UserRole.STUDENT) {
            user.setStudentUniversity(dto.getStudentUniversity());
        }

        return user;
    }

  
    public static UserResponseDTO toUserResponse(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        
        
        
        dto.setCity(user.getCity());
        dto.setProvince(user.getProvince());
        

        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setAddress(user.getAddress());
        dto.setGender(user.getGender());
        dto.setRole(user.getRole());
        
        if (user.getRole() == UserRole.TECHNICIAN) {
            dto.setCity(user.getCity());
            dto.setProvince(user.getProvince());
            
            if (user.getBasePrice() != null) {
                dto.setBasePrice(BigDecimal.valueOf(user.getBasePrice()));
            }
            if (user.getSkills() != null) {
                List<String> skillStrings = user.getSkills().stream()
                        .map(Enum::name) // Converts Enum to its String representation
                        .collect(Collectors.toList());
                dto.setSkills(skillStrings);
            }
            
            dto.setTechnicianAverageRating(user.getTechnicianAverageRating() != null 
                    ? user.getTechnicianAverageRating() 
                    : BigDecimal.ZERO);            dto.setTechnicianTotalJobs(user.getTechnicianTotalJobs() != null ? user.getTechnicianTotalJobs() : 0);
            
        }

        if (user.getRole() == UserRole.OWNER) {
            dto.setVerifiedOwner(user.isVerifiedOwner());
            dto.setSubscription_id(user.getSubscription_id());
            dto.setAccNo(user.getAccNo());
        }

        if (user.getRole() == UserRole.STUDENT) {
            dto.setStudentUniversity(user.getStudentUniversity());
        }

        return dto;
    }

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

