package com.budget.users.controller;

import com.budget.common.dto.AuthenticatedDTO;
import com.budget.users.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@PreAuthorize(("hasAnyAuthority('user', 'admin')"))
public class UserController {
    private final UserRepository repo;

    public UserController (UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping ("/session-verification")
    public String whoAmI (@AuthenticationPrincipal AuthenticatedDTO user) {
        String username = user.getUsername();
        System.out.println(username);
        return repo.whoAmI(username);
    }
}
