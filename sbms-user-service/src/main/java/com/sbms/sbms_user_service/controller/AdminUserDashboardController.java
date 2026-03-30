package com.sbms.sbms_user_service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_monolith.dto.admin.AdminUserDashboardDTO;
import com.sbms.sbms_user_service.enums.UserRole;
import com.sbms.sbms_user_service.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserDashboardController {

    private final UserRepository userRepository;

    public AdminUserDashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public AdminUserDashboardDTO dashboard() {

        long totalUsers = userRepository.count();
        long totalStudents = userRepository.countByRole(UserRole.STUDENT);
        long totalOwners = userRepository.countByRole(UserRole.OWNER);
        long unverifiedOwners =
                userRepository.countByRoleAndVerifiedOwnerFalse(UserRole.OWNER);

        return AdminUserDashboardDTO.builder()
                .totalUsers(totalUsers)
                .totalStudents(totalStudents)
                .totalOwners(totalOwners)
                .unverifiedOwners(unverifiedOwners)
                .build();
    }
}
