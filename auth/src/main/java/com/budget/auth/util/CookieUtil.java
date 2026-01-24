package com.budget.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.access.expiry}")
    private Long access;
    @Value("${jwt.refresh.expiry}")
    private Long refresh;

    public ResponseCookie addAccessCookie (String jwt){
        return ResponseCookie.from("access", jwt)
                .httpOnly(true)
                .secure(false) //TODO: Change to true in prod
                .path("/")
                .maxAge(access / 1000)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie addRefreshCookie (String jwt){
        return ResponseCookie.from("refresh", jwt)
                .httpOnly(true)
                .secure(false) //TODO: Change to true in prod
                .path("/")
                .maxAge(refresh / 1000)
                .sameSite("Lax")
                .build();
    }
}
