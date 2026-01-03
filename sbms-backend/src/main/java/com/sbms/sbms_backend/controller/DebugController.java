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

import com.sbms.sbms_backend.repository.UserRepository;

@RestController
@RequestMapping("/internal-debug")
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/user/{email}")
    public ResponseEntity<?> checkUser(@PathVariable String email, @RequestParam(required=false) String raw) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user-not-found");
        var user = userOpt.get();
        Map<String,Object> out = new HashMap<>();
        out.put("email", user.getEmail());
        out.put("passwordHash", user.getPassword()); // show hash only temporarily
        if (raw != null) out.put("matches", passwordEncoder.matches(raw, user.getPassword()));
        return ResponseEntity.ok(out);
    }
}
