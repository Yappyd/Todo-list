package com.yappyd.authservice.controller;

import com.yappyd.authservice.dto.RefreshRequest;
import com.yappyd.authservice.dto.TokenResponse;
import com.yappyd.authservice.dto.LoginRequest;
import com.yappyd.authservice.service.AuthService;
import com.yappyd.authservice.service.JwtService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody LoginRequest req) {
        log.info("Register request for username={}", req.username());

        TokenResponse response = authService.register(req);
        log.info("User registered successfully username={}", req.username());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        log.info("Login request received for username={}", req.username());

        TokenResponse response = authService.login(req);
        log.info("Login successful for username={}", req.username());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshRequest req) {
        log.debug("Refresh token request for token={}", req.refreshToken().substring(0, 10) + "...");

        TokenResponse response = authService.refreshToken(req);
        log.info("Refresh token successful for username={}", jwtService.extractUsername(response.accessToken()));

        return ResponseEntity.ok(response);
    }
}
