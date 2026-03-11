package com.budget.gateway.client;

import com.budget.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "cacheUsernames", url = "${routes.users}", configuration = FeignConfig.class)
public interface CacheUsernamesClient {
    @RequestMapping(method = RequestMethod.GET, value = "/update-cache")
    ResponseEntity<List<String>> getUsernames ();
}
