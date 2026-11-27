package com.rehan.contacts.controller;

import com.rehan.contacts.dto.AuthResponse;
import com.rehan.contacts.dto.LoginRequest;
import com.rehan.contacts.dto.RegisterRequest;
import com.rehan.contacts.dto.ResendRequest;
import com.rehan.contacts.dto.VerifyRequest;
import com.rehan.contacts.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registered. Enter the 5-digit code."));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(@Valid @RequestBody VerifyRequest req) {
        authService.verify(req);
        return ResponseEntity.ok(Map.of("message", "Account verified"));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, String>> resendCode(@Valid @RequestBody ResendRequest req) {
        authService.resendCode(req);
        return ResponseEntity.ok(Map.of("message", "A new code has been sent"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}