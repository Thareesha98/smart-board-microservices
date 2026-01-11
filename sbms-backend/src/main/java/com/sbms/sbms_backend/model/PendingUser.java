package com.sbms.sbms_backend.model;

import com.sbms.sbms_backend.model.enums.Gender;
import com.sbms.sbms_backend.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pending_users")
public class PendingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private Gender gender;
    private String nicNumber;
    private String accNo;
    private String studentUniversity;
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
