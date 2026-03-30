package com.sbms.sbms_user_service.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_monolith.dto.admin.AdminUserResponseDTO;
import com.sbms.sbms_monolith.dto.admin.UserVerificationDTO;
import com.sbms.sbms_user_service.service.AdminUserService;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // ---------------------------------------------------------
    // ALL USERS
    // ---------------------------------------------------------
    @GetMapping
    public List<AdminUserResponseDTO> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    // ---------------------------------------------------------
    // VERIFY OWNER
    // ---------------------------------------------------------
    @PutMapping("/{userId}/verify-owner")
    public void verifyOwner(
            @PathVariable Long userId,
            @RequestBody UserVerificationDTO dto
    ) {
        adminUserService.verifyOwner(userId, dto);
    }
}
