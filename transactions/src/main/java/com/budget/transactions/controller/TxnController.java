package com.budget.transactions.controller;


import com.budget.common.dto.AuthenticatedDTO;
import com.budget.common.dto.BudgetCatDTO;
import com.budget.transactions.model.CategoryDTO;
import com.budget.transactions.model.UpdateBudgetConfigDTO;
import com.budget.transactions.service.BudgetConfigService;
import com.budget.transactions.service.TxnCacheFacade;
import com.budget.transactions.service.WarmupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@PreAuthorize("hasAnyAuthority('user', 'admin')")
public class TxnController {
    private final WarmupService service;
    private final BudgetConfigService cfgService;
    private final TxnCacheFacade cacheFacade;

    public TxnController(WarmupService service,
                         TxnCacheFacade cacheFacade,
                         BudgetConfigService cfgService){
        this.service = service;
        this.cacheFacade = cacheFacade;
        this.cfgService = cfgService;
    }

    @PostMapping("/warmup")
    @PreAuthorize("hasAuthority('app')")
    public void warmup(@RequestBody String username){

        cacheFacade.warmUpFacade(username);
        System.out.println("Warming up. username: " + username);
    }

    @GetMapping("/get-init-dash")
    public List<CategoryDTO> getInitDash(@AuthenticationPrincipal AuthenticatedDTO user){
        String username = user.getUsername();
        return cacheFacade.getCache(username);
    }

    @PostMapping ("/activate-account")
    public List<BudgetCatDTO> activateAccount (@RequestBody List<UpdateBudgetConfigDTO> initIncome,
                                               @AuthenticationPrincipal AuthenticatedDTO user) {
        String username = user.getUsername();
        return cfgService.activateUser(initIncome, username);
    }

    @PostMapping ("/update-budget-config")
    public String updateBudgetConfig (@RequestBody List<UpdateBudgetConfigDTO> data,
                                      @AuthenticationPrincipal AuthenticatedDTO user){
        String username = user.getUsername();
        return cfgService.updateBudgetConfig(data, username);
    }

}
