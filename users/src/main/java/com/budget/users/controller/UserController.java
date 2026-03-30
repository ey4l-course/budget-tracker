package com.budget.users.controller;

import com.budget.common.dto.AuthenticatedDTO;
import com.budget.users.model.UpdateBudgetConfigDTO;
import com.budget.users.repository.UserRepository;
import com.budget.users.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @PostMapping ("activate-account")
    public HashMap<String, BigDecimal> activateAccount (@RequestBody UpdateBudgetConfigDTO dto,
                                                        @AuthenticationPrincipal AuthenticatedDTO user) {
        if (dto.getUsername() == null)
            dto.setUsername(user.getUsername());
        return service.activateAccount(dto);
    }

    @PostMapping ("/update-budget-config")
    public String updateBudgetConfig (@RequestBody UpdateBudgetConfigDTO[] configs,
                                      @AuthenticationPrincipal AuthenticatedDTO user) {
        List<UpdateBudgetConfigDTO> list = new ArrayList<>();
        for (UpdateBudgetConfigDTO conf : configs){
            if (conf.getUsername() == null)
                conf.setUsername(user.getUsername());
            list.add(conf);
        }
        return service.updateBudgetConfig(list);
    }
}
