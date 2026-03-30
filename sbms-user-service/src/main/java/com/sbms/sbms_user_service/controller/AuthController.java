package com.sbms.sbms_user_service.controller;


import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.sbms.sbms_user_service.dto.auth.JwtAuthResponse;
import com.sbms.sbms_user_service.dto.auth.OtpVerifyRequest;
import com.sbms.sbms_user_service.dto.auth.RefreshTokenRequest;
import com.sbms.sbms_user_service.dto.auth.ResetPasswordRequest;
import com.sbms.sbms_user_service.dto.user.UserLoginDTO;
import com.sbms.sbms_user_service.dto.user.UserRegisterDTO;
import com.sbms.sbms_user_service.model.RefreshToken;
import com.sbms.sbms_user_service.model.User;
import com.sbms.sbms_user_service.repository.UserRepository;
import com.sbms.sbms_user_service.security.JwtService;
import com.sbms.sbms_user_service.service.RefreshTokenService;
import com.sbms.sbms_user_service.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    // ---------------------------------------------------------
    // LOGIN
    // ---------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    dto.getEmail(),
                                    dto.getPassword()
                            )
                    );

            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            String jwt = jwtService.generateToken(userDetails);
            RefreshToken refreshToken =
                    refreshTokenService.createRefreshToken(user);

            JwtAuthResponse response = new JwtAuthResponse();
            response.setToken(jwt);
            response.setRefreshToken(refreshToken.getToken());
            response.setUser(userService.getUser(user.getId()));

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            log.warn("Login failed: bad credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    // ---------------------------------------------------------
    // REFRESH TOKEN
    // ---------------------------------------------------------
    @PostMapping("/refresh")
    public JwtAuthResponse refresh(@RequestBody RefreshTokenRequest request) {

        RefreshToken token =
                refreshTokenService.findByToken(request.getRefreshToken());

        refreshTokenService.verifyExpiration(token);

        User user = token.getUser();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_" + user.getRole().name())
                        .build();

        String newJwt = jwtService.generateToken(userDetails);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(newJwt);
        response.setRefreshToken(token.getToken());
        response.setUser(userService.getUser(user.getId()));

        return response;
    }

    // ---------------------------------------------------------
    // REGISTER REQUEST (OTP)
    // ---------------------------------------------------------
    @PostMapping("/register/request")
    public String registerRequest(@RequestBody UserRegisterDTO dto) {
        return userService.registerRequest(dto);
    }

    // ---------------------------------------------------------
    // VERIFY OTP + COMPLETE REGISTRATION
    // ---------------------------------------------------------
    @PostMapping("/register/verify")
    public JwtAuthResponse verifyOtp(@RequestBody OtpVerifyRequest req) {

        var userDto =
                userService.verifyRegistration(
                        req.getEmail(),
                        req.getOtp()
                );

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails ud =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_" + user.getRole().name())
                        .build();

        String jwt = jwtService.generateToken(ud);
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(jwt);
        response.setRefreshToken(refreshToken.getToken());
        response.setUser(userDto);

        return response;
    }

    // ---------------------------------------------------------
    // FORGOT PASSWORD
    // ---------------------------------------------------------
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ResetPasswordRequest req) {
        return userService.forgotPassword(req.getEmail());
    }

    // ---------------------------------------------------------
    // RESET PASSWORD
    // ---------------------------------------------------------
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest req) {
        return userService.resetPassword(
                req.getEmail(),
                req.getOtp(),
                req.getNewPassword()
        );
    }
}
