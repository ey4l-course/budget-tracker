package com.budget.gateway.client;

import com.budget.common.dto.AuthenticatedDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "publicUser", url = "${routes.auth}/public")
public interface PublicUserClient {
    @RequestMapping(method = RequestMethod.POST, value = "/{path}", consumes = "application/json")
    ResponseEntity<String> forward (@PathVariable("path") String path,
                                              @RequestBody String body);

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<String> refresh (@RequestBody AuthenticatedDTO user);
}
