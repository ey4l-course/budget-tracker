package com.budget.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "getWellKnown", url = "${routes.auth}/.well-known")
public interface GetWellKnown {
    @GetMapping(value = "/{path}")
    ResponseEntity<String> getAsset(@PathVariable ("path") String path);
}
