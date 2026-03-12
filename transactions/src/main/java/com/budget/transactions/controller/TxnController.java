package com.budget.transactions.controller;


import com.budget.common.utilities.SignatureHandlerUtil;
import com.budget.transactions.service.TxnService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/txn")
public class TxnController {
    private final TxnService service;

    public TxnController(TxnService service){ this.service = service; }

    @PostMapping("/warmup")
    public void warmup(@RequestHeader("X-internal-Auth") String header,
                       @RequestBody int userID){
        System.out.println(service.warmup(userID));
    }
}
