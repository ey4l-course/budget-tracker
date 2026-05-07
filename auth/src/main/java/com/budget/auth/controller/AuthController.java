package com.budget.auth.controller;

import com.budget.auth.service.IdentityService;
import com.budget.auth.util.CookieUtil;
import com.budget.common.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/public")
public class AuthController {
    private final IdentityService identityService;
    private final CookieUtil cookieUtil;

    public AuthController (IdentityService identityService,
                           CookieUtil cookieUtil){
        this.identityService = identityService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<FeignRegisterDTO> register (@RequestBody RegisterDto user){
        return ResponseEntity.status(HttpStatus.CREATED).body(identityService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<FeignLoginDTO> login (@RequestBody LoginDto credentials,
                                                   HttpServletRequest request){
        FeignLoginDTO res = identityService.login(credentials, request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.addAccessCookie(credentials.getAccess()))
                .header(HttpHeaders.SET_COOKIE, cookieUtil.addRefreshCookie(credentials.getRefresh()))
                .body(res);
    }

    @PostMapping("/refresh")
    public List<String> refresh (AuthenticatedDTO user){ return List.of(identityService.getNewToken(user)); }
}
