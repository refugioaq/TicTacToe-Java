package org.example.domain.service.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.domain.model.User;

import java.util.Date;

public class JwtProvider {
    private final SecretKey key = Keys.hmacShaKeyFor(("secret key: 8-800-555-35-35." +
            "Proshe pozvonit' chem y kogo-to zanimat'").getBytes());

    public Claims getClaim(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.login())
                .claim("type", "access")
                .claim("userId", user.userId().toString())
                .claim("role", user.role().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.login())
                .claim("type", "refresh")
                .claim("userId", user.userId().toString())
                .issuedAt(new Date((System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)))
                .signWith(key)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        Claims claims = getClaim(token);
        return "access".equals(claims.get("type"));
    }

    public boolean validateRefreshToken(String token) {
        Claims claims = getClaim(token);
        return "refresh".equals(claims.get("type"));
    }
}
