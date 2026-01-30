package com.budget.auth.controller;

import com.budget.auth.service.IdentityService;
import com.budget.auth.util.CookieUtil;
import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.LoginDto;
import com.budget.common.dto.RegisterDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final IdentityService identityService;

    public AuthController (IdentityService identityService){
        this.identityService = identityService;
    }

    @PostMapping("/register")
    public ResponseEntity<FeignResponseDTO> register (@RequestBody RegisterDto user){
        FeignResponseDTO res = identityService.register(user);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginDto credentials){
        FeignResponseDTO res = identityService.login(credentials);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, credentials.getAccess())
                .header(HttpHeaders.SET_COOKIE, credentials.getRefresh())
                .body(res);
    }
}
