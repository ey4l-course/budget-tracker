package com.budget.gateway.client;

import com.budget.gateway.config.FeignCookieRelay;
import com.budget.gateway.dto.FetchDashDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "txn-service", url = "${routes.transactions}", configuration = FeignCookieRelay.class)
public interface TxnClient {
    @GetMapping("/login")
    String getTxn ();

    @PostMapping(value = "/activate-account", consumes = MediaType.APPLICATION_JSON_VALUE)
    String ActivateAccount(@RequestBody String initPoll);

    @PostMapping(value = "/update-budget-config", consumes = MediaType.APPLICATION_JSON_VALUE)
    String updateBudgetConfig (@RequestBody String updatedData);

    @GetMapping(value = "/get-dash", consumes = MediaType.APPLICATION_JSON_VALUE)
    String getDashboard(@RequestParam ("month") String month);

    @GetMapping(value = "/get-budget-configs", consumes = MediaType.APPLICATION_JSON_VALUE)
    String fetchBudgetConfigs ();

    @PostMapping(value = "/new-txn", consumes = MediaType.APPLICATION_JSON_VALUE)
    String newTxn(@RequestBody String transactions);
}
