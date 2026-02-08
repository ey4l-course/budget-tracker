package com.budget.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiry}")
    private Long accessExpiry;

    @Value("${jwt.refresh.expiry}")
    private Long refreshExpiry;

    private SecretKey getKey () {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken (String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .claim("role", role)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiry))
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken (String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .claim("role", role)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiry))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername (String jwt) {
        return extractClaims(jwt, Claims::getSubject);
    }

    public String extractRole (String jwt) {
        return extractClaims(jwt, claims -> claims.get("role", String.class));
    }

    private <T> T extractClaims(String jwt, Function<Claims, T> claimResolver) {
        return claimResolver.apply(extractAllClaims(jwt));
    }

    private Claims extractAllClaims (String jwt) {
        return (Claims) Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
