package com.budget.auth.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final PrivateKey secret;

    @Value("${jwt.access.expiry}")
    private Long accessExpiry;
    @Value("${jwt.refresh.expiry}")
    private Long refreshExpiry;

    public JwtUtil (PrivateKey secret){
        this.secret = secret;
    }

    public String generateAccessToken (String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .claim("role", role)
                .claim("type", "access")
                .subject(username)
                .issuer("budget")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiry))
                .signWith(secret, Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken (String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .claim("role", role)
                .claim("type", "refresh")
                .subject(username)
                .issuer("budget")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiry))
                .signWith(secret, Jwts.SIG.RS256)
                .compact();
    }
}
