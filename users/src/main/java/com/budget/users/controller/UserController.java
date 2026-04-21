package com.budget.users.controller;

import com.budget.common.dto.AuthenticatedDTO;
import com.budget.users.repository.UserRepository;
import com.budget.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@PreAuthorize(("hasAnyAuthority('user', 'admin')"))
public class UserController {
    private final UserService service;
    private final UserRepository repo;

    public UserController (UserService service,
                           UserRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @GetMapping ("/session-verification")
    public String whoAmI (@AuthenticationPrincipal AuthenticatedDTO user) {
        String username = user.getUsername();
        return repo.whoAmI(username);
    }

    @PostMapping ("/activate-account")
    @PreAuthorize("hasAuthority('app')")
    public ResponseEntity<?> activateAccount (@RequestBody String username) {
        service.activateAccount(username);
        return ResponseEntity.ok(null);
    }

    @PostMapping ("/deactivate-account")
    @PreAuthorize("hasAuthority('app')")
    public ResponseEntity<?> deactivateAccount (@RequestBody String username) {
        service.deactivateAccount(username);
        return ResponseEntity.ok(null);
    }
}
