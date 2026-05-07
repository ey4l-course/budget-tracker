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

    public String addAccessCookie (String jwt){
        return ResponseCookie.from("access", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/app")
//                .maxAge(access / 1000)
                .sameSite("None")
                .build()
                .toString();
    }

    public String addRefreshCookie (String jwt){
        return ResponseCookie.from("refresh", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/public/refresh")
                .maxAge(refresh / 1000)
                .sameSite("None")
                .build()
                .toString();
    }

    public String removeAccessCookie (String jwt){
        return ResponseCookie.from("access", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/app")
                .maxAge(0)
                .sameSite("None")
                .build()
                .toString();
    }

    public String removeRefreshCookie (String jwt){
        return ResponseCookie.from("refresh", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/public/refresh")
                .maxAge(0)
                .sameSite("None")
                .build()
                .toString();
    }
}
