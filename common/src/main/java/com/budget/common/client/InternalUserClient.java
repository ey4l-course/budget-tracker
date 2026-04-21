package com.budget.common.client;

import com.budget.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "internalUserClient", url = "${routes.users}", configuration = FeignConfig.class)
public interface InternalUserClient {
    @PostMapping("/activate-account")
    ResponseEntity<?> activateAccount (@RequestBody String username);

    @PostMapping("/deactivate-account")
    ResponseEntity<?> deactivateAccount (@RequestBody String username);
}
