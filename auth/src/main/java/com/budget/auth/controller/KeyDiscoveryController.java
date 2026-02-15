package com.budget.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;
import java.util.Base64;

@RestController
@RequestMapping("/.well-known")
public class KeyDiscoveryController {
    private final PublicKey key;

    public KeyDiscoveryController (PublicKey key){ this.key = key; }

    @GetMapping("/public-key")
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok(Base64.getEncoder().encodeToString(key.getEncoded()));
    }
}
