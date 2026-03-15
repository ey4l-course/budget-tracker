package com.budget.gateway.client;

import com.budget.gateway.config.FeignCookieRelay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "txn-service", url = "${routes.transactions}", configuration = FeignCookieRelay.class)
public interface TxnClient {
    @GetMapping("/txn/get-init-dash")
    String getTxn ();
}
