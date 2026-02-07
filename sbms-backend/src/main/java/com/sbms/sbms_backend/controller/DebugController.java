package com.sbms.sbms_backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_backend.client.UserClient;
import com.sbms.sbms_backend.dto.user.UserMinimalDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal-debug")
@RequiredArgsConstructor // Better than @Autowired for constructor injection
public class DebugController {

    // FIX: Use Client, not Repository
    private final UserClient userClient;

    @GetMapping("/user/{email}")
    public ResponseEntity<?> checkUser(@PathVariable String email) {
        
        // 1. Fetch user from User Service via Client
        UserMinimalDTO user = userClient.findByEmail(email);
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user-not-found-in-user-service");
        }

        // 2. Prepare debug output
        Map<String, Object> out = new HashMap<>();
        out.put("id", user.getId());
        out.put("email", user.getEmail());
        out.put("role", user.getRole());
        out.put("verified", user.isVerifiedOwner());
        out.put("source", "User Service via Internal API");

        return ResponseEntity.ok(out);
    }
}