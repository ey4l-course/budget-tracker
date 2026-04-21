package com.budget.gateway.client;

import com.budget.gateway.config.FeignCookieRelay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "txn-service", url = "${routes.transactions}", configuration = FeignCookieRelay.class)
public interface TxnClient {
    @GetMapping("/get-init-dash")
    String getTxn ();

    @PostMapping(value = "/activate-account", consumes = MediaType.APPLICATION_JSON_VALUE)
    String ActivateAccount(@RequestBody String initPoll);

    @PostMapping(value = "/update-budget-config", consumes = MediaType.APPLICATION_JSON_VALUE)
    String updateBudgetConfig (@RequestBody String updatedData);

}
