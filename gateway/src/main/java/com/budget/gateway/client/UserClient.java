package com.budget.gateway.client;

import com.budget.gateway.config.FeignCookieRelay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service", url = "${routes.users}", configuration = FeignCookieRelay.class)
public interface UserClient {
    @GetMapping("/session-verification")
    String whoAmI ();

    @PostMapping(value = "activate-account", consumes = MediaType.APPLICATION_JSON_VALUE)
    String ActivateAccount(String initPoll);

    @PostMapping("/update-budget-config")
    String updateBudgetConfig (String updatedData);
}