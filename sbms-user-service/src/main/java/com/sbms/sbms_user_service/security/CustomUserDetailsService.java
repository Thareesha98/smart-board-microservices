package com.sbms.sbms_user_service.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.sbms.sbms_user_service.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.sbms.sbms_user_service.repository.UserRepository;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
 
    	 User user = userRepository.findByEmail(email)
                 .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

         String roleName = "ROLE_" + user.getRole().name();

         return org.springframework.security.core.userdetails.User.builder()
                 .username(user.getEmail())
                 .password(user.getPassword())
                 .authorities(List.of(new SimpleGrantedAuthority(roleName)))
                 .build();
     }
    
}
