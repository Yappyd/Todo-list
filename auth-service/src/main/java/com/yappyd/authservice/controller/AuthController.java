package com.yappyd.authservice.controller;

import com.yappyd.authservice.dto.RefreshRequest;
import com.yappyd.authservice.dto.TokenResponse;
import com.yappyd.authservice.dto.LoginRequest;
import com.yappyd.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody LoginRequest req) {
        TokenResponse response = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login (@Valid @RequestBody LoginRequest req) {
        TokenResponse response = authService.login(req);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken (@Valid @RequestBody RefreshRequest req) {
        TokenResponse response = authService.refreshToken(req);
        return ResponseEntity.ok(response);
    }
}
