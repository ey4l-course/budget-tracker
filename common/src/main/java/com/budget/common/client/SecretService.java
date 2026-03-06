package com.budget.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "secretService", url = "${routes.auth}/internal")
public interface SecretService {
    @PostMapping (value = "/sign-me{SERVICE}")
    ResponseEntity<String> getSigned (@RequestHeader ("X-bootstrap-secret") String providedSecret,
                                      @RequestParam ("service") String SERVICE);
}
