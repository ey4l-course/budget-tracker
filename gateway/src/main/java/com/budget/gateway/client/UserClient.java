package com.budget.gateway.client;

import com.budget.gateway.config.FeignCookieRelay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service", url = "${routes.users}", configuration = FeignCookieRelay.class)
public interface UserClient {
    @GetMapping("/session-verification")
    String whoAmI ();
}