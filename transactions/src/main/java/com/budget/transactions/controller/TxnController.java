package com.budget.transactions.controller;


import com.budget.common.dto.AuthenticatedDTO;
import com.budget.transactions.model.CategoryDTO;
import com.budget.transactions.service.TxnCacheFacade;
import com.budget.transactions.service.TxnService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/txn")
public class TxnController {
    private final TxnService service;
    private final TxnCacheFacade cacheFacade;

    public TxnController(TxnService service,
                         TxnCacheFacade cacheFacade){
        this.service = service;
        this.cacheFacade = cacheFacade;
    }

    @PostMapping("/warmup")
    @PreAuthorize("hasAuthority('app')")
    public void warmup(@RequestHeader("X-internal-Auth") String header,
                       @RequestBody String username){
        System.out.println("warmup request landed");
        List<CategoryDTO> tst = cacheFacade.warmUpFacade(username);
        System.out.println(tst);
    }

    @GetMapping("/get-init-dash")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    public List<CategoryDTO> getInitDash(@AuthenticationPrincipal AuthenticatedDTO user){
        String username = user.getUsername();
        return cacheFacade.getCache(username);
    }
}
