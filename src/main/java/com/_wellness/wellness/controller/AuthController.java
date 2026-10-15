package com._wellness.wellness.controller;

import com._wellness.wellness.dto.RegisterRequest;
import com._wellness.wellness.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request) {

        try {
            String message = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
@PostMapping("/login")
public ResponseEntity<String> login(
        @RequestBody com._wellness.wellness.dto.LoginRequest request) {

    try {
        String token = authService.login(request);
        return ResponseEntity.ok(token);

    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }
}

@GetMapping("/check")
public ResponseEntity<String> check() {
    return ResponseEntity.ok("JWT authentication successful");
}
}