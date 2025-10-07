package com.yappyd.authservice.service;

import com.yappyd.authservice.dto.LoginRequest;
import com.yappyd.authservice.dto.RefreshRequest;
import com.yappyd.authservice.dto.TokenResponse;
import com.yappyd.authservice.exception.IncorrectPasswordException;
import com.yappyd.authservice.exception.InvalidTokenException;
import com.yappyd.authservice.exception.UsernameAlreadyExistsException;
import com.yappyd.authservice.exception.UsernameNotFoundException;
import com.yappyd.authservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";
    private static final String VALID_ACCESS_TOKEN = "validAccessToken";
    private static final String VALID_REFRESH_TOKEN = "validRefreshToken";
    private static final long ACCESS_TOKEN_EXPIRES_IN = 3600L;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username(TEST_USERNAME)
                .passwordHash(ENCODED_PASSWORD)
                .build();
    }

    @Test
    void register_withNewUsername_returnsTokenResponse() {
        LoginRequest req = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);
        when(userService.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(jwtService.generateAccessToken(TEST_USERNAME)).thenReturn(VALID_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(TEST_USERNAME)).thenReturn(VALID_REFRESH_TOKEN);
        when(jwtService.getAccessExpirationSeconds()).thenReturn(ACCESS_TOKEN_EXPIRES_IN);

        TokenResponse result = authService.register(req);

        assertThat(result.accessToken()).isEqualTo(VALID_ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(VALID_REFRESH_TOKEN);
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.accessTokenExpiresIn()).isEqualTo(ACCESS_TOKEN_EXPIRES_IN);

        verify(userService).existsByUsername(TEST_USERNAME);
        verify(userService).saveUser(req);
        verify(jwtService).generateAccessToken(TEST_USERNAME);
        verify(jwtService).generateRefreshToken(TEST_USERNAME);
        verify(jwtService).getAccessExpirationSeconds();
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void register_withExistingUsername_throwsUsernameAlreadyExistsException() {
        LoginRequest req = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);
        when(userService.existsByUsername(TEST_USERNAME)).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining(TEST_USERNAME);

        verify(userService).existsByUsername(TEST_USERNAME);
        verifyNoMoreInteractions(userService, jwtService, passwordEncoder);
    }

    @Test
    void login_withValidCredentials_returnsTokenResponse() {
        LoginRequest req = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);
        when(userService.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateAccessToken(TEST_USERNAME)).thenReturn(VALID_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(TEST_USERNAME)).thenReturn(VALID_REFRESH_TOKEN);
        when(jwtService.getAccessExpirationSeconds()).thenReturn(ACCESS_TOKEN_EXPIRES_IN);

        TokenResponse result = authService.login(req);

        assertThat(result.accessToken()).isEqualTo(VALID_ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(VALID_REFRESH_TOKEN);
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.accessTokenExpiresIn()).isEqualTo(ACCESS_TOKEN_EXPIRES_IN);

        verify(userService).findByUsername(TEST_USERNAME);
        verify(passwordEncoder).matches(TEST_PASSWORD, ENCODED_PASSWORD);
        verify(jwtService).generateAccessToken(TEST_USERNAME);
        verify(jwtService).generateRefreshToken(TEST_USERNAME);
        verify(jwtService).getAccessExpirationSeconds();
    }

    @Test
    void login_withNonExistingUsername_throwsUsernameNotFoundException() {
        LoginRequest req = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);
        when(userService.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(TEST_USERNAME);

        verify(userService).findByUsername(TEST_USERNAME);
        verifyNoMoreInteractions(userService, passwordEncoder, jwtService);
    }

    @Test
    void login_withIncorrectPassword_throwsIncorrectPasswordException() {
        LoginRequest req = new LoginRequest(TEST_USERNAME, "wrongPassword");
        when(userService.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", ENCODED_PASSWORD)).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(IncorrectPasswordException.class)
                .hasMessageContaining(TEST_USERNAME);

        verify(userService).findByUsername(TEST_USERNAME);
        verify(passwordEncoder).matches("wrongPassword", ENCODED_PASSWORD);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void refreshToken_withValidRefreshToken_returnsTokenResponse() {
        RefreshRequest req = new RefreshRequest(VALID_REFRESH_TOKEN);
        doNothing().when(jwtService).validateToken(VALID_REFRESH_TOKEN, JwtService.TokenRole.REFRESH);
        when(jwtService.extractUsername(VALID_REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
        when(jwtService.generateAccessToken(TEST_USERNAME)).thenReturn(VALID_ACCESS_TOKEN);
        when(jwtService.getAccessExpirationSeconds()).thenReturn(ACCESS_TOKEN_EXPIRES_IN);

        TokenResponse result = authService.refreshToken(req);

        assertThat(result.accessToken()).isEqualTo(VALID_ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(VALID_REFRESH_TOKEN);
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.accessTokenExpiresIn()).isEqualTo(ACCESS_TOKEN_EXPIRES_IN);

        verify(jwtService).validateToken(VALID_REFRESH_TOKEN, JwtService.TokenRole.REFRESH);
        verify(jwtService).extractUsername(VALID_REFRESH_TOKEN);
        verify(jwtService).generateAccessToken(TEST_USERNAME);
        verify(jwtService).getAccessExpirationSeconds();
        verifyNoInteractions(userService, passwordEncoder);
    }

    @Test
    void refreshToken_withInvalidRefreshToken_throwsInvalidTokenException() {
        RefreshRequest req = new RefreshRequest("invalidToken");
        doThrow(new InvalidTokenException("Invalid token", JwtService.TokenRole.REFRESH)).when(jwtService)
                .validateToken("invalidToken", JwtService.TokenRole.REFRESH);

        assertThatThrownBy(() -> authService.refreshToken(req))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid refresh token");

        verify(jwtService).validateToken("invalidToken", JwtService.TokenRole.REFRESH);
        verifyNoMoreInteractions(jwtService, userService, passwordEncoder);
    }
}