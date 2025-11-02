package com.estatement.service;

import com.estatement.dto.PasswordChangeRequest;
import com.estatement.entity.User;
import com.estatement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public void changePassword(PasswordChangeRequest request) {
        // Get the currently logged-in user's username
        Authentication currentUserAuth = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUserAuth.getName();

        // 1. VERIFY CURRENT PASSWORD
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getCurrentPassword())
            );
        } catch (Exception e) {
            // If it fails, the password was wrong.
            throw new BadCredentialsException("Invalid current password");
        }

        // 2. GET USER & HASH NEW PASSWORD
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String newHashedPassword = passwordEncoder.encode(request.getNewPassword());

        // 3. UPDATE AND SAVE
        user.setPasswordHash(newHashedPassword);
        userRepository.save(user);
    }
}