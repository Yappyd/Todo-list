package com.yappyd.authservice.service;

import com.yappyd.authservice.dto.LoginRequest;
import com.yappyd.authservice.model.User;
import com.yappyd.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username(TEST_USERNAME)
                .passwordHash(ENCODED_PASSWORD)
                .build();
    }

    @Test
    void findByUsername_withExistingUser_returnsUser() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByUsername(TEST_USERNAME);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(result.get().getPasswordHash()).isEqualTo(ENCODED_PASSWORD);

        verifyNoInteractions(passwordEncoder);
        verify(userRepository).findByUsername(TEST_USERNAME);
    }

    @Test
    void findByUsername_withNonExistingUser_returnsEmptyOptional() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername(TEST_USERNAME);

        assertThat(result).isEmpty();

        verifyNoInteractions(passwordEncoder);
        verify(userRepository).findByUsername(TEST_USERNAME);
    }

    @ParameterizedTest
    @CsvSource({
            "testuser, true",
            "nonexistent, false"
    })
    void existsByUsername_withExistingUsers_ExpectedResult(String username, boolean expectedResult) {
        when(userRepository.existsByUsername(username)).thenReturn(expectedResult);

        boolean result = userService.existsByUsername(username);

        assertThat(result).isEqualTo(expectedResult);

        verifyNoInteractions(passwordEncoder);
        verify(userRepository).existsByUsername(username);
    }

    @Test
    void saveUser_withValidRequest_savesUser() {
        LoginRequest req = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        userService.saveUser(req);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(TEST_PASSWORD);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(savedUser.getPasswordHash()).isEqualTo(ENCODED_PASSWORD);
    }
}