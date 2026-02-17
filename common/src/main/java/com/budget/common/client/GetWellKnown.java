package com.budget.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "getWellKnown", url = "/.well-known")
public interface GetWellKnown {
    @GetMapping(value = "/${path}")
    ResponseEntity<String> getAsset(String path);
}
