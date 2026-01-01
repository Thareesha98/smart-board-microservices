package com.sbms.sbms_backend.controller;

import com.sbms.sbms_backend.dto.auth.JwtAuthResponse;
import com.sbms.sbms_backend.dto.auth.OtpVerifyRequest;
import com.sbms.sbms_backend.dto.auth.RefreshTokenRequest;
import com.sbms.sbms_backend.dto.auth.ResetPasswordRequest;
import com.sbms.sbms_backend.dto.user.UserLoginDTO;
import com.sbms.sbms_backend.dto.user.UserRegisterDTO;
import com.sbms.sbms_backend.dto.user.UserResponseDTO;
import com.sbms.sbms_backend.model.RefreshToken;
import com.sbms.sbms_backend.model.User;
import com.sbms.sbms_backend.repository.UserRepository;
import com.sbms.sbms_backend.security.JwtService;
import com.sbms.sbms_backend.service.RefreshTokenService;
import com.sbms.sbms_backend.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j // <--- Add this
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;


    // ---------------------------------------------------------
    // REGISTER + return accessToken + refreshToken
    // ---------------------------------------------------------
//    @PostMapping("/register")
//    public JwtAuthResponse register(@RequestBody UserRegisterDTO dto) {
//
//        UserResponseDTO userDto = userService.register(dto);
//
//        User user = userRepository.findByEmail(userDto.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found after registration"));
//
//        UserDetails userDetails = org.springframework.security.core.userdetails.User
//                .withUsername(user.getEmail())
//                .password(user.getPassword())
//                .authorities("ROLE_" + user.getRole().name())
//                .build();
//
//        String jwt = jwtService.generateToken(userDetails);
//        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
//
//        JwtAuthResponse response = new JwtAuthResponse();
//        response.setToken(jwt);
//        response.setRefreshToken(refreshToken.getToken());
//        response.setUser(userDto);
//
//        return response;
//    }


    // ---------------------------------------------------------
    // LOGIN + return accessToken + refreshToken
    // ---------------------------------------------------------
//   

    
    
    
    
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

            Authentication auth = authenticationManager.authenticate(authToken); // may throw

            // if we reach here, authentication succeeded
            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities("ROLE_" + user.getRole().name())
                    .build();

            String jwt = jwtService.generateToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            UserResponseDTO userDto = userService.getUser(user.getId());

            JwtAuthResponse response = new JwtAuthResponse();
            response.setToken(jwt);
            response.setRefreshToken(refreshToken.getToken());
            response.setUser(userDto);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            // wrong password
            log.warn("Login failed for {}: bad credentials", dto.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (DisabledException ex) {
            log.warn("Login failed for {}: account disabled", dto.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account disabled");
        } catch (AuthenticationException ex) {
            // generic authentication failure
            log.warn("Authentication error for {}: {}", dto.getEmail(), ex.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        } catch (Exception ex) {
            log.error("Unexpected error during login for {}: {}", dto.getEmail(), ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    


    // ---------------------------------------------------------
    // REFRESH ACCESS TOKEN
    // POST /api/auth/refresh
    // body: { "refreshToken": "..." }
    // ---------------------------------------------------------
    @PostMapping("/refresh")
    public JwtAuthResponse refresh(@RequestBody RefreshTokenRequest request) {

        String requestToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestToken);
        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String newJwt = jwtService.generateToken(userDetails);

        UserResponseDTO userDto = userService.getUser(user.getId());

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(newJwt);
        response.setRefreshToken(requestToken); // reuse same refresh token
        response.setUser(userDto);

        return response;
    }
    
    

    // STEP 1: Register request → sends OTP
    @PostMapping("/register/request")
    public String registerRequest(@RequestBody UserRegisterDTO dto) {
        return userService.registerRequest(dto);
    }

    // STEP 2: Verify OTP & complete registration
    @PostMapping("/register/verify")
    public JwtAuthResponse verifyOtp(@RequestBody OtpVerifyRequest req) {

        UserResponseDTO userDto = userService.verifyRegistration(req.getEmail(), req.getOtp());

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails ud = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String jwt = jwtService.generateToken(ud);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(jwt);
        response.setRefreshToken(refreshToken.getToken());
        response.setUser(userDto);

        return response;
    }

    // FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ResetPasswordRequest req) {
        return userService.forgotPassword(req.getEmail());
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest req) {
        return userService.resetPassword(
                req.getEmail(),
                req.getOtp(),
                req.getNewPassword()
        );
    }


    
    
}
