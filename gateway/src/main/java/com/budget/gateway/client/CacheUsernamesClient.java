package com.budget.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "cacheUsernames", url = "${routes.users}")
public interface CacheUsernamesClient {
    @RequestMapping(method = RequestMethod.GET, value = "/getAllUsernames", consumes = "application/json")
    ResponseEntity<List<String>> getUsernames ();
}
