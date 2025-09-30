package com.yappyd.authservice.service;

import com.yappyd.authservice.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
public class JwtService {
    @Value("${security.jwt.secret}")
    private String secret;
    @Value("${security.jwt.access-expiration}")
    private Duration accessExpiration;
    @Value("${security.jwt.refresh-expiration}")
    private Duration refreshExpiration;
    @Value("${security.jwt.issuer:auth-service}")
    private String issuer;

    public String generateAccessToken(String username) {
        return buildToken(username, accessExpiration);
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, refreshExpiration);
    }

    private String buildToken(String username, Duration expiration) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration.toMillis());

        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public void isTokenValid(String token, TokenRole type) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token);
        }
        catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token expired", type, e);
        }
        catch (JwtException e) {
            throw new InvalidTokenException("Invalid token", type, e);
        }
    }

    public long getAccessExpirationSeconds() {
        return accessExpiration.toSeconds();
    }
}