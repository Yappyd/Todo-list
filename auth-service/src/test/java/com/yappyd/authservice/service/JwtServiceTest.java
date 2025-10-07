package com.yappyd.authservice.service;

import com.yappyd.authservice.exception.InvalidTokenException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

import static io.jsonwebtoken.SignatureAlgorithm.RS256;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    private JwtService jwtService;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private String issuer = "auth-service";
    private Duration accessExpiration = Duration.ofSeconds(3600);
    private Duration refreshExpiration = Duration.ofSeconds(604800);

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        KeyPair keyPair = Keys.keyPairFor(RS256);
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        ReflectionTestUtils.setField(jwtService, "privateKey", privateKey);
        ReflectionTestUtils.setField(jwtService, "publicKey", publicKey);
        ReflectionTestUtils.setField(jwtService, "issuer", issuer);
        ReflectionTestUtils.setField(jwtService, "accessExpiration", accessExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshExpiration);
        ReflectionTestUtils.setField(jwtService, "privateKeyPath", "dummy/path/private.pem");
        ReflectionTestUtils.setField(jwtService, "publicKeyPath", "dummy/path/public.pem");
    }

    @Test
    void generateAccessToken_withValidUsername_returnsValidToken() {
        String username = "testuser";

        String token = jwtService.generateAccessToken(username);

        JwtParser parser = Jwts.parser().verifyWith(publicKey).build();
        var claims = parser.parseSignedClaims(token).getPayload();
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.getIssuer()).isEqualTo(issuer);
        assertThat(claims.get("token_role")).isEqualTo(JwtService.TokenRole.ACCESS.name());
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
        assertThat(claims.getExpiration().getTime())
                .isGreaterThanOrEqualTo(System.currentTimeMillis() + accessExpiration.toMillis() - 1000);
    }

    @Test
    void generateRefreshToken_withValidUsername_returnsValidToken() {
        String username = "testuser";

        String token = jwtService.generateRefreshToken(username);

        JwtParser parser = Jwts.parser().verifyWith(publicKey).build();
        var claims = parser.parseSignedClaims(token).getPayload();
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.getIssuer()).isEqualTo(issuer);
        assertThat(claims.get("token_role")).isEqualTo(JwtService.TokenRole.REFRESH.name());
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
        assertThat(claims.getExpiration().getTime())
                .isGreaterThanOrEqualTo(System.currentTimeMillis() + refreshExpiration.toMillis() - 1000);
    }

    @Test
    void extractUsername_withValidToken_returnsUsername() {
        String username = "testuser";

        String token = jwtService.generateAccessToken(username);

        String extractedUsername = jwtService.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void extractUsername_withInvalidToken_throwsInvalidTokenException() {
        String invalidToken = "invalid.token.string";
        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid token: Extract username exception");
    }

    @Test
    void validateToken_withValidAccessToken_succeeds() {
        String token = jwtService.generateAccessToken("testuser");

        jwtService.validateToken(token, JwtService.TokenRole.ACCESS);
    }

    @Test
    void validateToken_withRefreshTokenForAccess_fails() {
        String token = jwtService.generateRefreshToken("testuser");

        assertThatThrownBy(() -> jwtService.validateToken(token, JwtService.TokenRole.ACCESS))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid access token: Invalid token type");
    }

    @Test
    void validateToken_withExpiredToken_fails() {
        ReflectionTestUtils.setField(jwtService, "accessExpiration", Duration.ofSeconds(-3600));

        String token = jwtService.generateAccessToken("testuser");

        assertThatThrownBy(() -> jwtService.validateToken(token, JwtService.TokenRole.ACCESS))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid access token: Token expired");
    }

    @Test
    void validateToken_withInvalidToken_fails() {
        String invalidToken = "invalidToken";

        assertThatThrownBy(() -> jwtService.validateToken(invalidToken, JwtService.TokenRole.ACCESS))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid access token: Invalid token");
    }

    @Test
    void getAccessExpirationSeconds_returnsCorrectValue() {
        long expirationSeconds = jwtService.getAccessExpirationSeconds();

        assertThat(expirationSeconds).isEqualTo(accessExpiration.toSeconds());
    }
}