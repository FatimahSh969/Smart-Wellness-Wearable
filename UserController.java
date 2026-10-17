package com._wellness.wellness.controller;

import com._wellness.wellness.repository.SensorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final SensorRepository sensorRepository;

    public UserController(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @GetMapping("/check")
    public String checkUserAccess() {
        return "User access granted";
    }

    @GetMapping("/dashboard/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getUserDashboard(
            @PathVariable Long userId,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        return ResponseEntity.ok(
                "Welcome to user dashboard, secured for: " + currentEmail
        );
    }

    @DeleteMapping("/data")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<String> deleteMyData(
            Authentication authentication) {

        String currentEmail = authentication.getName();

               int deleted =
    sensorRepository.deleteByUserIdValue("demo-user");

        return ResponseEntity.ok(
                "Deleted sensor readings: " + deleted
        );
    }
}