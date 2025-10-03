package com.yappyd.authservice.service;

import com.yappyd.authservice.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    public enum TokenRole {
        ACCESS,
        REFRESH
    }
    private static final String TOKEN_ROLE_CLAIM = "token_role";

    @Value("${security.jwt.private-key}")
    private String privateKeyPath;
    @Value("${security.jwt.public-key}")
    private String publicKeyPath;
    @Value("${security.jwt.access-expiration}")
    private Duration accessExpiration;
    @Value("${security.jwt.refresh-expiration}")
    private Duration refreshExpiration;
    @Value("${security.jwt.issuer:auth-service}")
    private String issuer;

    private RSAPrivateKey privateKey;
    @Getter
    private RSAPublicKey publicKey;

    @PostConstruct
    public void init() {
        log.info("Loading RSA keys from files");

        try (PemReader reader = new PemReader(new InputStreamReader(Files.newInputStream(Paths.get(privateKeyPath))))) {
            byte[] content = reader.readPemObject().getContent();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(content);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateKey) kf.generatePrivate(spec);
            log.info("Private key loaded successfully");

        } catch (Exception e) {
            log.error("Failed to load private key", e);

            throw new IllegalStateException("Failed to load private key", e);
        }

        try (PemReader reader = new PemReader(new InputStreamReader(Files.newInputStream(Paths.get(publicKeyPath))))) {
            byte[] content = reader.readPemObject().getContent();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(content);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = (RSAPublicKey) kf.generatePublic(spec);
            log.info("Public key loaded successfully (modulus={} bits)", publicKey.getModulus().bitLength());

        } catch (Exception e) {
            log.error("Failed to load public key", e);

            throw new IllegalStateException("Failed to load public key", e);
        }
    }

    public String generateAccessToken(String username) {
        log.info("Generating access token for username={}", username);

        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_ROLE_CLAIM, TokenRole.ACCESS.name());
        String token = buildToken(username, accessExpiration, claims);
        log.debug("Access token generated: {}", token.substring(0, 10) + "...");

        return token;
    }

    public String generateRefreshToken(String username) {
        log.info("Generating refresh token for username={}", username);

        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_ROLE_CLAIM, TokenRole.REFRESH.name());
        String token = buildToken(username, refreshExpiration, claims);
        log.debug("Refresh token generated: {}", token.substring(0, 10) + "...");

        return token;
    }

    private String buildToken(String username, Duration expiration, Map<String, Object> claims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration.toMillis());

        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiry)
                .claims(claims)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String extractUsername(String token) {
        log.debug("Extracting username from token");

        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public void validateToken(String token, TokenRole type) {
        log.debug("Validating {} token", type.name().toLowerCase());

        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = jws.getPayload();
            String typeClaim = claims.get(TOKEN_ROLE_CLAIM, String.class);

            if (typeClaim == null || !typeClaim.equals(type.name())) {
                throw new InvalidTokenException("Invalid token type", type);
            }
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token expired", type, e);
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid token", type, e);
        }
    }

    public long getAccessExpirationSeconds() {
        log.debug("Getting access expiration seconds");

        return accessExpiration.toSeconds();
    }
}