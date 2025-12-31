package com.sbms.sbms_backend.controller;

import com.sbms.sbms_backend.dto.user.*;
import com.sbms.sbms_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;


    // ---------------------------------------------------------
    // REGISTER (student or owner)
    // POST /api/users/register
    // ---------------------------------------------------------
    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRegisterDTO dto) {
        return userService.register(dto);
    }


    // ---------------------------------------------------------
    // LOGIN (simple — JWT will replace this)
    // POST /api/users/login
    // ---------------------------------------------------------
    @PostMapping("/login")
    public UserResponseDTO login(@RequestBody UserLoginDTO dto) {
        return userService.login(dto);
    }


    // ---------------------------------------------------------
    // GET PROFILE (for dashboard)
    // GET /api/users/{id}
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }


    // ---------------------------------------------------------
    // UPDATE PROFILE
    // PUT /api/users/{id}
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRegisterDTO dto) {
        return userService.updateUser(id, dto);
    }


    // ---------------------------------------------------------
    // OWNER PROFILE
    // GET /api/users/owner/{ownerId}
    // ---------------------------------------------------------
    @GetMapping("/owner/{ownerId}")
    public OwnerProfileDTO getOwnerProfile(@PathVariable Long ownerId) {
        return userService.getOwnerProfile(ownerId);
    }


    // ---------------------------------------------------------
    // ADMIN: GET ALL USERS
    // GET /api/users/all
    // ---------------------------------------------------------
    @GetMapping("/all")
    public List<AdminUserDTO> getAllUsers() {
        return userService.getAllUsers();
    }


    // ---------------------------------------------------------
    // ADMIN: GET ALL OWNERS
    // GET /api/users/owners
    // ---------------------------------------------------------
    @GetMapping("/owners")
    public List<AdminUserDTO> getAllOwners() {
        return userService.getAllOwners();
    }
}
