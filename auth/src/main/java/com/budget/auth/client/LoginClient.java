package com.budget.auth.client;

import com.budget.common.dto.InternalFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient (name = "loginClient", url = "${routes.public}")
public interface LoginClient {
    @RequestMapping(method = RequestMethod.GET, value = "/login/{username}")
    ResponseEntity<InternalFeignDTO> login(@PathVariable("username") String username);
}
