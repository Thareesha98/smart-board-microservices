package com.sbms.sbms_user_service.controller;

import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_user_service.dto.internal.UserMinimalDTO;
import com.sbms.sbms_user_service.dto.internal.UserRoleDTO;
import com.sbms.sbms_user_service.dto.internal.UserSnapshotDTO;
import com.sbms.sbms_user_service.model.User;
import com.sbms.sbms_user_service.repository.UserRepository;

@RestController
@RequestMapping("/api/internal/users")
public class InternalUserController {

    private final UserRepository userRepository;

    public InternalUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ---------------------------------------------------------
    // CHECK USER EXISTS
    // ---------------------------------------------------------
    @GetMapping("/exists/{id}")
    public boolean userExists(@PathVariable Long id) {
        return userRepository.existsById(id);
    }

    // ---------------------------------------------------------
    // GET USER ROLE
    // ---------------------------------------------------------
    @GetMapping("/role/{id}")
    public UserRoleDTO getUserRole(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserRoleDTO dto = new UserRoleDTO();
        dto.setUserId(user.getId());
        dto.setRole(user.getRole());

        return dto;
    }

    // ---------------------------------------------------------
    // GET MINIMAL USER
    // ---------------------------------------------------------
    @GetMapping("/{id}/minimal")
    public UserMinimalDTO getMinimal(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserMinimalDTO dto = new UserMinimalDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setVerifiedOwner(user.isVerifiedOwner());

        return dto;
    }

    // ---------------------------------------------------------
    // FULL USER SNAPSHOT (ALL FIELDS)
    // ---------------------------------------------------------
    @GetMapping("/{id}/snapshot")
    public UserSnapshotDTO getSnapshot(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserSnapshotDTO dto = new UserSnapshotDTO();

        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setGender(user.getGender());
        dto.setNicNumber(user.getNicNumber());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        dto.setVerifiedOwner(user.isVerifiedOwner());
        dto.setSubscriptionId(user.getSubscription_id());
        dto.setAccNo(user.getAccNo());
        dto.setStudentUniversity(user.getStudentUniversity());
        dto.setBoardings(user.getBoardings());
        dto.setActive(true); // or derive from status later

        return dto;
    }
    
 // ---------------------------------------------------------
    // FIND BY EMAIL (FOR JWT RESOLUTION)
    // ---------------------------------------------------------
    @GetMapping("/by-email")
    public UserMinimalDTO getByEmail(@RequestParam String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        UserMinimalDTO dto = new UserMinimalDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setVerifiedOwner(user.isVerifiedOwner());

        return dto;
    }
}
