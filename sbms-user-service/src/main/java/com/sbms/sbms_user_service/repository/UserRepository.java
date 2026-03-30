package com.sbms.sbms_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_user_service.enums.UserRole;
import com.sbms.sbms_user_service.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);
    

  
    
    long countByRole(UserRole role);
    

    long countByRoleAndVerifiedOwnerFalse(UserRole role);

}
