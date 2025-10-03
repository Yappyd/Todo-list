package com.yappyd.authservice.service;

import com.yappyd.authservice.dto.LoginRequest;
import com.yappyd.authservice.model.User;
import com.yappyd.authservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        log.debug("Searching for user by username={}", username);

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            log.debug("User found: username={}", username);
        } else {
            log.debug("User not found: username={}", username);
        }
        return userOpt;
    }

    public boolean existsByUsername(String username) {
        log.debug("Checking existence of username={}", username);

        boolean exists = userRepository.existsByUsername(username);
        log.debug("Existence check result for username={}: {}", username, exists);

        return exists;
    }

    public void saveUser(LoginRequest req) {
        log.info("Saving new user: username={}", req.username());

        User user = User.builder()
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .build();
        userRepository.save(user);
        log.info("User saved successfully: username={}", req.username());
    }
}
