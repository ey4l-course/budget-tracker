package com.budget.transactions.controller;


import com.budget.common.dto.AuthenticatedDTO;
import com.budget.common.dto.BudgetCatDTO;
import com.budget.common.exceptions.CustomSecurityException;
import com.budget.transactions.model.CategoryDTO;
import com.budget.transactions.model.FetchDashDTO;
import com.budget.transactions.model.UpdateBudgetConfigDTO;
import com.budget.transactions.service.BudgetConfigService;
import com.budget.transactions.service.TxnCacheFacade;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping
@PreAuthorize("hasAnyAuthority('user', 'admin')")
public class TxnController {
    private final BudgetConfigService cfgService;
    private final TxnCacheFacade cacheFacade;

    public TxnController(TxnCacheFacade cacheFacade,
                         BudgetConfigService cfgService){
        this.cacheFacade = cacheFacade;
        this.cfgService = cfgService;
    }

    @PostMapping("/warmup")
    @PreAuthorize("hasAuthority('app')")
    public void warmup(@RequestBody String username){
        FetchDashDTO dto = setCurrentMonth(username);
        cacheFacade.warmUpFacade(dto);
        System.out.println("Warming up. username: " + username);
    }

    @GetMapping("/login")
    public List<CategoryDTO> login (@AuthenticationPrincipal AuthenticatedDTO user){
        FetchDashDTO dto = setCurrentMonth(user.getUsername());
        return cacheFacade.getCache(dto);
    }

    @GetMapping ("/get-dash")
    public List<CategoryDTO> getDashData (@AuthenticationPrincipal AuthenticatedDTO user,
                                          @RequestParam(required = false, name = "month") @DateTimeFormat(pattern = "yyyy-MM")YearMonth month){
        FetchDashDTO dto = new FetchDashDTO(
                user.getUsername(),
                month.atDay(1).atStartOfDay(),
                month.plusMonths(1).atDay(1).atStartOfDay()
        );
        return cacheFacade.getCache(dto);
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

    //helper
    private FetchDashDTO setCurrentMonth (String username){
        LocalDateTime now = LocalDateTime.now();
        return new FetchDashDTO(
                username,
                now.withDayOfMonth(1).toLocalDate().atStartOfDay(),
                now.plusMonths(1)
        );
    }

}
