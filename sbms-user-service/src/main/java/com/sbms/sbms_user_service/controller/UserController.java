package com.sbms.sbms_user_service.controller;


import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_user_service.dto.user.AdminUserDTO;
import com.sbms.sbms_user_service.dto.user.OwnerProfileDTO;
import com.sbms.sbms_user_service.dto.user.UserRegisterDTO;
import com.sbms.sbms_user_service.dto.user.UserResponseDTO;
import com.sbms.sbms_user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    
    
    @PutMapping("/profile")
    public UserResponseDTO updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UserRegisterDTO dto
    ) {
        return userService.updateUser(userId, dto);
    }

    
    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @PathVariable Long id,
            @RequestBody UserRegisterDTO dto
    ) {
        return userService.updateUser(id, dto);
    }

    
    @GetMapping("/owner/{ownerId}")
    public OwnerProfileDTO getOwnerProfile(
            @PathVariable Long ownerId
    ) {
        return userService.getOwnerProfile(ownerId);
    }

    @GetMapping("/all")
    public List<AdminUserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

   
    @GetMapping("/owners")
    public List<AdminUserDTO> getAllOwners() {
        return userService.getAllOwners();
    }
}
