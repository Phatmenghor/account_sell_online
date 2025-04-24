package com.account_sell.config;

import com.account_sell.enumation.RoleEnum;
import com.account_sell.enumation.StatusData;
import com.account_sell.feature.auth.models.Role;
import com.account_sell.feature.auth.models.UserEntity;
import com.account_sell.feature.auth.repository.RoleRepository;
import com.account_sell.feature.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Initializes default roles and users on application startup.
 * This runs when the application starts and ensures required data exists.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultUserInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.default-users.create:true}")
    private boolean createDefaultUsers;

    @Value("${app.developer.email:developer@example.com}")
    private String developerEmail;

    @Value("${app.developer.password:developer123}")
    private String developerPassword;

    @Value("${app.user.email:user@example.com}")
    private String userEmail;

    @Value("${app.user.password:user123}")
    private String userPassword;

    @Override
    public void run(String... args) {
        log.info("Initializing default roles and users...");

        // Create each role if it doesn't already exist
        initializeRoles();

        // Only create default users if enabled in config
        if (createDefaultUsers) {
            // Create default users if they don't exist
            createDefaultAdminUser();
            createDefaultDeveloperUser();
            createDefaultRegularUser();
        } else {
            log.info("Default user creation is disabled");
        }

        log.info("Role and user initialization completed");
    }

    /**
     * Initialize all required roles
     */
    private void initializeRoles() {
        Arrays.stream(RoleEnum.values()).forEach(roleEnum -> {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = new Role();
                role.setName(roleEnum);
                roleRepository.save(role);
                log.info("Created role: {}", roleEnum);
            } else {
                log.debug("Role already exists: {}", roleEnum);
            }
        });
    }

    /**
     * Creates a default admin user if no admin exists.
     */
    private void createDefaultAdminUser() {
        // Skip if user already exists
        if (userRepository.existsByUsername(adminEmail)) {
            log.info("Admin user already exists: {}", adminEmail);
            return;
        }

        createUserWithRole(adminEmail, adminPassword, RoleEnum.ADMIN);
    }

    /**
     * Creates a default developer user if none exists.
     */
    private void createDefaultDeveloperUser() {
        // Skip if user already exists
        if (userRepository.existsByUsername(developerEmail)) {
            log.info("Developer user already exists: {}", developerEmail);
            return;
        }

        createUserWithRole(developerEmail, developerPassword, RoleEnum.DEVELOPER);
    }

    /**
     * Creates a default regular user if none exists.
     */
    private void createDefaultRegularUser() {
        // Skip if user already exists
        if (userRepository.existsByUsername(userEmail)) {
            log.info("Regular user already exists: {}", userEmail);
            return;
        }

        createUserWithRole(userEmail, userPassword, RoleEnum.USER);
    }

    /**
     * Helper method to create a user with a specific role
     */
    private void createUserWithRole(String username, String password, RoleEnum roleEnum) {
        try {
            // Get the role directly from the database
            Role role = roleRepository.findByName(roleEnum)
                    .orElseThrow(() -> {
                        log.error("{} role not found, cannot create user", roleEnum);
                        return new RuntimeException(roleEnum + " role not found");
                    });

            // Create new user - IMPORTANT: Initialize with new ArrayList
            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setStatus(StatusData.ACTIVE);
            user.setPassword(passwordEncoder.encode(password));

            // Create the roles list and add the role
            List<Role> roles = new ArrayList<>();
            roles.add(role);
            user.setRoles(roles);

            // Save the user with the role directly
            userRepository.save(user);

            log.info("Created user: {} with role: {}", username, roleEnum);
        } catch (Exception e) {
            log.error("Error creating user {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }
}