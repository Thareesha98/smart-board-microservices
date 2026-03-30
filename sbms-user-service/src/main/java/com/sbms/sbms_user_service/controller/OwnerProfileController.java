package com.sbms.sbms_user_service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_user_service.dto.profile.OwnerProfileUpdateDTO;
import com.sbms.sbms_user_service.dto.profile.ProfileResponseDTO;
import com.sbms.sbms_user_service.service.ProfileService;

@RestController
@RequestMapping("/api/owner/profile")
@PreAuthorize("hasRole('OWNER')")
public class OwnerProfileController {

    private final ProfileService profileService;

    public OwnerProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileResponseDTO get(Authentication auth) {
        return profileService.getProfile(auth.getName());
    }

    @PutMapping
    public ProfileResponseDTO update(
            @RequestBody OwnerProfileUpdateDTO dto,
            Authentication auth
    ) {
        return profileService.updateOwnerProfile(auth.getName(), dto);
    }
}
