
package com._wellness.wellness.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/check")
    public String checkUserAccess() {
        return "User access granted";
    }

    @GetMapping("/dashboard/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getUserDashboard(@PathVariable Long userId, Authentication authentication) {
        String currentEmail = (String) authentication.getPrincipal();
        
        return ResponseEntity.ok("Welcome to user dashboard, secured for: " + currentEmail);
    }

}
