package com.yappyd.authservice.controller;

import com.yappyd.authservice.dto.AuthResponse;
import com.yappyd.authservice.dto.LoginRequest;
import com.yappyd.authservice.dto.RegisterRequest;
import com.yappyd.authservice.model.Role;
import com.yappyd.authservice.model.User;
import com.yappyd.authservice.security.JwtService;
import com.yappyd.authservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userService.existsByUsername(req.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        // создаём пользователя с ролью ROLE_USER
        User user = User.builder()
                .username(req.getUsername())
                .password(req.getPassword()) // будет захэширован в UserService.save()
                .role(Role.ROLE_USER)
                .build();

        User saved = userService.save(user);

        // сразу выдаём токен
        String role = saved.getRole().name();
        String token = jwtService.generateToken(saved.getUsername(), role);
        long expiresIn = jwtService.getRemainingMs(token).orElse(0L);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, "Bearer", expiresIn));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

            // пароль ок, роль берём из auth
            String role = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_USER");

            String token = jwtService.generateToken(req.getUsername(), role);
            long expiresIn = jwtService.getRemainingMs(token).orElse(0L);

            return ResponseEntity.ok(new AuthResponse(token, "Bearer", expiresIn));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
