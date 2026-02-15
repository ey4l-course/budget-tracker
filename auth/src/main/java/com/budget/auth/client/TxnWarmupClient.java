package com.budget.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "txnWarmup", url = "${routs.transactions}")
public interface TxnWarmupClient {
    @PostMapping("/aux/warmup")
    void warmup (@RequestHeader("X-internal-Auth") String header);
}
