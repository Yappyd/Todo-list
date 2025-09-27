package com.yappyd.authservice.service;

import com.yappyd.authservice.dto.LoginRequest;
import com.yappyd.authservice.dto.TokenResponse;
import com.yappyd.authservice.dto.RefreshRequest;
import com.yappyd.authservice.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponse register(LoginRequest req) {
        if (userService.existsByUsername(req.username())) {
            throw new IllegalArgumentException("username is already exists");
        }
        User user = User.builder()
                .username(req.username())
                .password(req.password())
                .build();
        userService.saveUser(user);
        String accessToken = jwtService.generateAccessToken(req.username());
        String refreshToken = jwtService.generateRefreshToken(req.username());
        return new TokenResponse(accessToken, refreshToken, "Bearer", jwtService.getAccessExpirationSeconds());
    }

    public TokenResponse login(LoginRequest req) {
        User user = userService.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        String accessToken = jwtService.generateAccessToken(req.username());
        String refreshToken = jwtService.generateRefreshToken(req.username());
        return new TokenResponse(accessToken, refreshToken, "Bearer", jwtService.getAccessExpirationSeconds());
    }

    public TokenResponse refreshToken(RefreshRequest req) {
        if (!jwtService.isTokenValid(req.token())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String username = jwtService.extractUsername(req.token());

        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        return new TokenResponse(newAccessToken, newRefreshToken, "Bearer",
                jwtService.getAccessExpirationSeconds());
    }
}
