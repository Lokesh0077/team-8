package com.estatement.service;

import com.estatement.entity.User;
import com.estatement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.estatement.dto.UserDto;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        String password = new String(user.getPasswordHash());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), password, new ArrayList<>());
    }


    public UserDto getCurrentUserProfile() {
        // 1. Get the username from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new RuntimeException("No authenticated user found.");
        }
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        // 2. Find the user in the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 3. Map User entity to UserDto
        return UserDto.builder()
                .id(user.getId())
                .name(user.getUsername()) // Mapping 'username' from User to 'name' in Dto
                .email(user.getEmail())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


}