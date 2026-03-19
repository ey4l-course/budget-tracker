package com.budget.auth.client;

import com.budget.common.dto.FeignLoginDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient (name = "loginClient", url = "${routes.users}")
public interface LoginClient {
    @GetMapping(value = "/login/{username}")
    ResponseEntity<FeignLoginDTO> login(@RequestHeader("X-internal-Auth") String header,
                                        @PathVariable("username") String username);
}
