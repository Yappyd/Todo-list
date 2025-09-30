package com.yappyd.authservice.service;

import com.yappyd.authservice.dto.LoginRequest;
import com.yappyd.authservice.model.User;
import com.yappyd.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void saveUser(LoginRequest req) {
        User user = User.builder()
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .build();
        userRepository.save(user);
    }
}
