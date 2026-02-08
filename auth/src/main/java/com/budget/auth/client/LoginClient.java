package com.budget.auth.client;

import com.budget.common.dto.InternalFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient (name = "loginClient", url = "${routes.public}")
public interface LoginClient {
    @GetMapping(value = "/login/{username}")
    ResponseEntity<InternalFeignDTO> login(@PathVariable("username") String username);
}
