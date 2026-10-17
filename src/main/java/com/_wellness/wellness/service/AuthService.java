
package com._wellness.wellness.service;

import com._wellness.wellness.dto.LoginRequest;
import com._wellness.wellness.dto.RegisterRequest;
import com._wellness.wellness.model.Role;
import com._wellness.wellness.model.User;
import com._wellness.wellness.repository.UserRepository;
import com._wellness.wellness.security.JwtService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    // Register a new user

    public String register(RegisterRequest request) {

            // Register a new user
if (request.getName() == null || request.getName().isBlank()
                || request.getEmail() == null
                || !request.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
                || request.getPassword() == null
                || request.getPassword().length() < 8) {
            throw new IllegalArgumentException(
                    "Invalid name, email, or password (min 8 characters)");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        if (!request.isConsentAccepted()) {
            throw new IllegalArgumentException(
                    "Consent is required");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));

        user.setAge(request.getAge());
        user.setRole(Role.User);
        user.setConsentAccepted(true);
        user.setConsentTime(Instant.now());

        userRepository.save(user);

        return "Account created successfully";
    }

    // Login an existing user
    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid email or password"));

        if (user.isDeleted()) {
            throw new IllegalArgumentException(
                    "Invalid email or password");
        }

        // Check whether the account is temporarily locked
        if (user.getLockedUntil() != null) {

            if (user.getLockedUntil().isAfter(Instant.now())) {
                throw new IllegalArgumentException(
                        "Account is temporarily locked. Try again later");
            }

            // Reset the lock after 15 minutes
            user.setLockedUntil(null);
            user.setFailedAttempts(0);
        }

        // Check the submitted password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            int attempts = user.getFailedAttempts() + 1;

            user.setFailedAttempts(attempts);

            // Lock the account after 5 failed attempts
            if (attempts >= 5) {
                user.setLockedUntil(
                        Instant.now().plusSeconds(15 * 60));
            }

            userRepository.save(user);

            throw new IllegalArgumentException(
                    "Invalid email or password");
        }

        // Reset failed attempts after successful login
        user.setFailedAttempts(0);
        user.setLockedUntil(null);

        userRepository.save(user);

        // Generate a JWT token
        return jwtService.generateToken(user);
    }
}
