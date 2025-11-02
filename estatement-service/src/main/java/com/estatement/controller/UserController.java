package com.estatement.controller;

import com.estatement.dto.PasswordChangeRequest;
import com.estatement.dto.UserDto;
import com.estatement.service.CustomUserDetailsService;
import com.estatement.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordService passwordService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile() {
        // Call the new method you added to your service
        UserDto userDto = customUserDetailsService.getCurrentUserProfile();
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            passwordService.changePassword(request);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));

        } catch (BadCredentialsException e) {
            // This catches the "Invalid current password" error
            // and returns the 401 status your frontend is looking for.
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));

        } catch (Exception e) {
            // Catches any other errors (e.g., user not found)
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred."));
        }
    }
}