package com.mallforge.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    @Value("${mall.jwt.secret}")
    private String secret;

    @Value("${mall.jwt.expiration:86400}")
    private long expirationSeconds;

    @Value("${mall.jwt.refresh-expiration:604800}")
    private long refreshExpirationSeconds;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, Long tenantId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tenantId", tenantId);
        claims.put("username", username);
        return build(claims, expirationSeconds);
    }

    public String generateRefreshToken(Long userId) {
        return build(Map.of("userId", userId, "type", "refresh"), refreshExpirationSeconds);
    }

    private String build(Map<String, Object> claims, long seconds) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + seconds * 1000))
                .signWith(getKey())
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser().verifyWith(getKey()).build()
                    .parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            log.warn("JWT parse failed: {}", e.getMessage());
            return null;
        }
    }

    public Long getUserId(String token) {
        Claims c = parseToken(token);
        return c == null ? null : c.get("userId", Long.class);
    }

    public Long getTenantId(String token) {
        Claims c = parseToken(token);
        return c == null ? null : c.get("tenantId", Long.class);
    }
}
