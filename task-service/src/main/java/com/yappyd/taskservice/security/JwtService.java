package com.yappyd.taskservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    private SecretKey signingKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        // генерация ключа
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                keyBytes = md.digest(keyBytes);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to initialize JWT signing key", e);
            }
        }
        // создание парсера
        signingKey = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parser()
                .setSigningKey(signingKey)
                .build();
    }

    // парсинг токена
    public Optional<Claims> parseClaims(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return Optional.ofNullable(claims);
        } catch (ExpiredJwtException e) {
            // просроченный токен
            return Optional.ofNullable(e.getClaims());
        } catch (JwtException | IllegalArgumentException e) {
            // некорректный токен
            return Optional.empty();
        }
    }

    // универсальный метод извлечения claim
    private <T> Optional<T> extractClaim(String token, Function<Claims, T> claimsResolver) {
        return parseClaims(token).map(claimsResolver);
    }

    // извлечь username
    public Optional<String> extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Optional<Date> extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // извлечь role
    public Optional<String> extractRole(String token) {
        return extractClaim(token, c -> c.get("role", String.class));
    }

    // оставшееся время жизни токена в миллисекундах (может быть отрицательным, если просрочен)
    public Optional<Long> getRemainingMs(String token) {
        return extractExpiration(token).map(exp -> exp.getTime() - System.currentTimeMillis());
    }

    // проверка валидности токена (парсинг, подпись, совпадает ли subject, срок)
    public boolean isTokenValid(String token, String username) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            String tokenUsername = claims.getSubject();
            Date exp = claims.getExpiration();
            return username != null && username.equals(tokenUsername) && exp != null && exp.after(new Date());
        } catch (ExpiredJwtException e) {
            // токен просрочен
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            // подпись неверна
            return false;
        }
    }
}
