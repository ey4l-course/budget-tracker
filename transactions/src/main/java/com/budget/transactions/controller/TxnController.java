package com.budget.transactions.controller;


import com.budget.common.utilities.SignatureHandlerUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/txn")
public class TxnController {
    private final SignatureHandlerUtil sigUtil;

    public TxnController(SignatureHandlerUtil sigUtil){ this.sigUtil = sigUtil; }

    @PostMapping("/warmup")
    public void warmup(@RequestHeader("X-internal-Auth") String header){
        String username = sigUtil.signatureVerification(header, false);
        //TODO: txnUtil.warmup(service);
    }
}
