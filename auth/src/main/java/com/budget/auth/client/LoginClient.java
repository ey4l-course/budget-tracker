package com.budget.auth.client;

import com.budget.common.dto.InternalFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient (name = "loginClient", url = "${routes.public}")
public interface LoginClient {
    @GetMapping(value = "/login/{service}")
    ResponseEntity<InternalFeignDTO> login(@RequestHeader("X-internal-Auth") String header,
                                           @PathVariable("service") String username);
}
