package com.estatement.config;

import com.estatement.entity.Role;
import com.estatement.entity.User;
import com.estatement.repository.RoleRepository;
import com.estatement.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        createRoleIfNotExists("ROLE_ADMIN");
        createRoleIfNotExists("ROLE_USER");
        createRoleIfNotExists("ROLE_AUDITOR");
        createDefaultAdmin();
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }

    private void createDefaultAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123@"));
            admin.setEmail("admin@estatement.com");
            admin.setRoles(Set.of(adminRole));
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("✅ Default admin user created: username='admin', password='admin123'");
        }
    }
}
