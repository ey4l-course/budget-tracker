package com.budget.auth.client;

import com.budget.common.dto.FeignResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "registerClient", url = "${routes.users}")
public interface RegisterClient {
    @RequestMapping(method = RequestMethod.POST, value = "/{path}", consumes = "application/json")
    ResponseEntity<FeignResponseDTO> register (@PathVariable ("path") String path,
                                               @RequestHeader("X-internal-Auth") String header,
                                               @RequestBody String body);
}
