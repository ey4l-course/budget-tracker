package com.budget.transactions.controller;


import com.budget.common.dto.AuthenticatedDTO;
import com.budget.common.dto.BudgetCatDTO;
import com.budget.common.exceptions.CustomSecurityException;
import com.budget.transactions.model.*;
import com.budget.transactions.service.BudgetConfigService;
import com.budget.transactions.service.TxnCacheFacade;
import com.budget.transactions.service.TxnService;
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
    private final TxnService service;

    public TxnController(TxnCacheFacade cacheFacade,
                         BudgetConfigService cfgService,
                         TxnService service){
        this.cacheFacade = cacheFacade;
        this.cfgService = cfgService;
        this.service = service;
    }

    @PostMapping("/warmup")
    @PreAuthorize("hasAuthority('app')")
    public void warmup(@RequestBody String username){
        FetchDashDTO dto = setCurrentMonth(username);
        cacheFacade.warmUpFacade(dto);
        cacheFacade.configWarmupFacade(username);
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
        System.out.println(month);
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

    @GetMapping ("/get-budget-configs")
    public List<BudgetCatDTO> getBudgetConfigs (@AuthenticationPrincipal AuthenticatedDTO user){
        String username = user.getUsername();
        return cacheFacade.getConfigCache(username);
    }

    @PostMapping ("/new-txn")
    public String newTxn (@RequestBody List<TransactionEntity> data,
                          @AuthenticationPrincipal AuthenticatedDTO user){
        for (TransactionEntity txn : data){
            txn.setUsername(user.getUsername());
        }
        return service.newTxn(data);
    }

    //helper
    private FetchDashDTO setCurrentMonth (String username){
        YearMonth now = YearMonth.now();
        return new FetchDashDTO(
                username,
                now.atDay(1).atStartOfDay(),
                now.plusMonths(1).atDay(1).atStartOfDay()
        );
    }

}
