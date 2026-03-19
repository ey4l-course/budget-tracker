package com.budget.auth.client;

import com.budget.common.dto.FeignRegisterDTO;
import com.budget.common.dto.RegisterDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "registerClient", url = "${routes.users}")
public interface RegisterClient {
    @RequestMapping(method = RequestMethod.POST, value = "/register", consumes = "application/json")
    ResponseEntity<FeignRegisterDTO> register (@RequestHeader("X-internal-Auth") String header,
                                               @RequestBody RegisterDto body);
}
