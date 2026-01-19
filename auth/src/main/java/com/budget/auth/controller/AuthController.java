package com.budget.auth.controller;

import com.budget.auth.service.IdentityService;
import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.RegisterDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final IdentityService identityService;

    public AuthController (IdentityService identityService){
        this.identityService = identityService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody RegisterDto user){
        FeignResponseDTO res = identityService.register(user);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
