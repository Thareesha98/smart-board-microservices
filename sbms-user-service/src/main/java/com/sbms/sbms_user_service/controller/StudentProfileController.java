package com.sbms.sbms_user_service.controller;


import com.sbms.sbms_user_service.dto.profile.CommonProfileUpdateDTO;
import com.sbms.sbms_user_service.dto.profile.OwnerProfileUpdateDTO;
import com.sbms.sbms_user_service.dto.profile.ProfileResponseDTO;
import com.sbms.sbms_user_service.dto.profile.StudentProfileUpdateDTO;
import com.sbms.sbms_user_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final ProfileService profileService;

    /**
     * Get the profile of the currently logged-in user.
     * The Gateway should extract the email from the JWT and pass it in the header.
     */
    @GetMapping("/me")
    public ProfileResponseDTO getMyProfile(@RequestHeader("X-User-Email") String email) {
        return profileService.getProfile(email);
    }

    /**
     * Update Student specific details.
     */
    @PutMapping("/student")
    public ProfileResponseDTO updateStudent(
            @RequestHeader("X-User-Email") String email,
            @RequestBody StudentProfileUpdateDTO dto
    ) {
        return profileService.updateStudentProfile(email, dto);
    }

    /**
     * Update Owner specific details.
     */
    @PutMapping("/owner")
    public ProfileResponseDTO updateOwner(
            @RequestHeader("X-User-Email") String email,
            @RequestBody OwnerProfileUpdateDTO dto
    ) {
        return profileService.updateOwnerProfile(email, dto);
    }

    /**
     * Update Admin/Common details.
     */
    @PutMapping("/admin")
    public ProfileResponseDTO updateAdmin(
            @RequestHeader("X-User-Email") String email,
            @RequestBody CommonProfileUpdateDTO dto
    ) {
        return profileService.updateAdminProfile(email, dto);
    }
}