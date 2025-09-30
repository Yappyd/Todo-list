package com.yappyd.authservice.service;

import com.yappyd.authservice.dto.LoginRequest;
import com.yappyd.authservice.dto.TokenResponse;
import com.yappyd.authservice.dto.RefreshRequest;
import com.yappyd.authservice.exception.IncorrectPasswordException;
import com.yappyd.authservice.exception.UsernameAlreadyExistsException;
import com.yappyd.authservice.exception.UsernameNotFoundException;
import com.yappyd.authservice.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final String TOKEN_TYPE = "Bearer";

    public AuthService(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponse register(LoginRequest req) {
        if (userService.existsByUsername(req.username())) {
            throw new UsernameAlreadyExistsException(req.username());
        }
        userService.saveUser(req);
        String accessToken = jwtService.generateAccessToken(req.username());
        String refreshToken = jwtService.generateRefreshToken(req.username());
        return new TokenResponse(accessToken, refreshToken, TOKEN_TYPE, jwtService.getAccessExpirationSeconds());
    }

    public TokenResponse login(LoginRequest req) {
        User user = userService.findByUsername(req.username())
                .orElseThrow(() -> new UsernameNotFoundException(req.username()));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IncorrectPasswordException(req.username());
        }
        String accessToken = jwtService.generateAccessToken(req.username());
        String refreshToken = jwtService.generateRefreshToken(req.username());
        return new TokenResponse(accessToken, refreshToken, TOKEN_TYPE, jwtService.getAccessExpirationSeconds());
    }

    public TokenResponse refreshToken(RefreshRequest req) {
        jwtService.isTokenValid(req.refreshToken(), TokenRole.REFRESH);
        String username = jwtService.extractUsername(req.refreshToken());
        String newAccessToken = jwtService.generateAccessToken(username);
        return new TokenResponse(newAccessToken, req.refreshToken(), TOKEN_TYPE, jwtService.getAccessExpirationSeconds());
    }
}
