package com.sbms.sbms_user_service.service;

import com.sbms.sbms_user_service.dto.profile.CommonProfileUpdateDTO;
import com.sbms.sbms_user_service.dto.profile.OwnerProfileUpdateDTO;
import com.sbms.sbms_user_service.dto.profile.ProfileResponseDTO;
import com.sbms.sbms_user_service.dto.profile.StudentProfileUpdateDTO;

public interface ProfileService {

    ProfileResponseDTO getProfile(String email);

    ProfileResponseDTO updateStudentProfile(String email, StudentProfileUpdateDTO dto);

    ProfileResponseDTO updateOwnerProfile(String email, OwnerProfileUpdateDTO dto);

    ProfileResponseDTO updateAdminProfile(String email, CommonProfileUpdateDTO dto);
}
