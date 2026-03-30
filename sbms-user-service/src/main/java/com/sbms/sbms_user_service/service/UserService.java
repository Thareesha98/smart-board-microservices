package com.sbms.sbms_user_service.service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sbms.sbms_user_service.client.FileClient;
import com.sbms.sbms_user_service.dto.user.AdminUserDTO;
import com.sbms.sbms_user_service.dto.user.OwnerProfileDTO;
import com.sbms.sbms_user_service.dto.user.UserLoginDTO;
import com.sbms.sbms_user_service.dto.user.UserRegisterDTO;
import com.sbms.sbms_user_service.dto.user.UserResponseDTO;
import com.sbms.sbms_user_service.enums.MaintenanceIssueType;
import com.sbms.sbms_user_service.enums.OtpPurpose;
import com.sbms.sbms_user_service.enums.UserRole;
import com.sbms.sbms_user_service.mapper.UserMapper;
import com.sbms.sbms_user_service.model.Otp;
import com.sbms.sbms_user_service.model.PendingUser;
import com.sbms.sbms_user_service.model.User;
import com.sbms.sbms_user_service.repository.PendingUserRepository;
import com.sbms.sbms_user_service.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PendingUserRepository pendingRepo;
    private final OtpService otpService;
    private final EmailService emailService;
    private final FileClient fileClient;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            PendingUserRepository pendingRepo,
            OtpService otpService,
            EmailService emailService,
            FileClient fileClient
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.pendingRepo = pendingRepo;
        this.otpService = otpService;
        this.emailService = emailService;
        this.fileClient = fileClient;
    }

    public UserResponseDTO register(UserRegisterDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = UserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User saved = userRepository.save(user);
        return UserMapper.toUserResponse(saved);
    }
    
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public UserResponseDTO login(UserLoginDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return UserMapper.toUserResponse(user);
    }

    public UserResponseDTO getUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserMapper.toUserResponse(user);
    }

    public UserResponseDTO updateUser(Long id, UserRegisterDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // PATCH style update
        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            user.setPhone(dto.getPhone());
        }

        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            user.setAddress(dto.getAddress());
        }

        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }

        if (dto.getNicNumber() != null) {
            user.setNicNumber(dto.getNicNumber());
        }

        // Technician fields
        if (user.getRole() == UserRole.TECHNICIAN) {

            if (dto.getCity() != null) {
                user.setCity(dto.getCity());
            }

            if (dto.getProvince() != null) {
                user.setProvince(dto.getProvince());
            }

            if (dto.getBasePrice() != null) {
                user.setBasePrice(dto.getBasePrice());
            }

            if (dto.getSkills() != null) {

                List<MaintenanceIssueType> enumSkills = new java.util.ArrayList<>();

                for (String skillStr : dto.getSkills()) {
                    try {
                        enumSkills.add(
                            MaintenanceIssueType.valueOf(skillStr.toUpperCase().trim())
                        );
                    } catch (IllegalArgumentException e) {
                        // ignore invalid skill
                    }
                }

                user.setSkills(enumSkills);
            }
        }

       
     // Avatar update (supports Base64 OR URL)
        if (dto.getProfileImageUrl() != null && !dto.getProfileImageUrl().isBlank()) {

            String imageData = dto.getProfileImageUrl();

            // If it is Base64 (from frontend)
            if (imageData.startsWith("data:image")) {

                try {

                    String base64String = imageData;
                    String contentType = "image/jpeg";

                    if (base64String.contains("data:image/png")) {
                        contentType = "image/png";
                    }

                    // remove data:image/... prefix
                    if (base64String.contains(",")) {
                        base64String = base64String.split(",")[1];
                    }

                    byte[] imageBytes = Base64.getDecoder().decode(base64String);

                    String extension = contentType.equals("image/png") ? ".png" : ".jpg";
                    String fileName = UUID.randomUUID() + extension;

                    // upload using FileClient
                    String imageUrl = fileClient.uploadBytes(
                            imageBytes,
                            fileName,
                            "profiles"
                    );

                    if (imageUrl != null) {
                        user.setProfileImageUrl(imageUrl);
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Avatar upload failed", e);
                }

            } else {

                // already a URL
                user.setProfileImageUrl(imageData);

            }
        }
        
        
        User saved = userRepository.save(user);

        return UserMapper.toUserResponse(saved);
    }


    public OwnerProfileDTO getOwnerProfile(Long ownerId) {

        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        if (user.getRole() != UserRole.OWNER) {
            throw new RuntimeException("User is not an owner");
        }

        return UserMapper.toOwnerProfile(user);
    }

    public List<AdminUserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toAdminUser)
                .collect(Collectors.toList());
    }

    public List<AdminUserDTO> getAllOwners() {
        return userRepository.findByRole(UserRole.OWNER)
                .stream()
                .map(UserMapper::toAdminUser)
                .collect(Collectors.toList());
    }


    public String registerRequest(UserRegisterDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        PendingUser pending = pendingRepo.findByEmail(dto.getEmail())
                .orElse(new PendingUser());
        
        
       
    

        pending.setFullName(dto.getFullName());
        pending.setEmail(dto.getEmail());
        pending.setPassword(passwordEncoder.encode(dto.getPassword()));
        pending.setPhone(dto.getPhone());
        pending.setAddress(dto.getAddress());
        pending.setGender(dto.getGender());
        pending.setNicNumber(dto.getNicNumber());
        pending.setAccNo(dto.getAccNo());
        pending.setStudentUniversity(dto.getStudentUniversity());
        pending.setRole(dto.getRole());
        
        
        
        
        if (dto.getRole() == UserRole.TECHNICIAN) {
            pending.setCity(dto.getCity());
            pending.setProvince(dto.getProvince());
            pending.setBasePrice(dto.getBasePrice());
            pending.setSkills(dto.getSkills());
            pending.setTechnicianAverageRating(BigDecimal.valueOf(0.0));
            pending.setTechnicianTotalJobs(0);
        }

       
        
        

        pendingRepo.save(pending);

        Otp otp = otpService.createRegistrationOtp(dto.getEmail());
        emailService.sendOtpEmail(dto.getEmail(), otp.getOtpCode());

        return "OTP sent to email!";
    }

    @Transactional
    public UserResponseDTO verifyRegistration(String email, String otpCode) {

        boolean valid = otpService.validateOtp(
                email,
                otpCode,
                OtpPurpose.REGISTRATION
        );

        if (!valid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        PendingUser p = pendingRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Registration already verified"));

        User user = new User();
        user.setFullName(p.getFullName());
        user.setEmail(p.getEmail());
        user.setPassword(p.getPassword());
        user.setPhone(p.getPhone());
        user.setAddress(p.getAddress());
        user.setGender(p.getGender());
        user.setNicNumber(p.getNicNumber());
        user.setAccNo(p.getAccNo());
        user.setStudentUniversity(p.getStudentUniversity());
        user.setRole(p.getRole());
        user.setVerifiedOwner(p.getRole() == UserRole.OWNER);

        User saved = userRepository.save(user);
        pendingRepo.delete(p);

        return UserMapper.toUserResponse(saved);
    }


    public String forgotPassword(String email) {

        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        Otp otp = otpService.createPasswordResetOtp(email);
        emailService.sendResetToken(email, otp.getOtpCode());

        return "Reset OTP sent to email";
    }

    public String resetPassword(String email, String otpCode, String newPassword) {

        boolean valid = otpService.validateOtp(
                email,
                otpCode,
                OtpPurpose.PASSWORD_RESET
        );

        if (!valid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password reset successful";
    }

   

    public String changePassword(String email, String currentPassword, String newPassword) {
        
        // 1. Fetch User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check if current password matches DB password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }

        // 3. Encode and Set New Password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password changed successfully";
    }
}
