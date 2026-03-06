package com.budget.gateway.client;

import com.budget.common.dto.FeignResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "publicUser", url = "${routes.auth}/public")
public interface PublicUserClient {
    @RequestMapping(method = RequestMethod.POST, value = "/{path}", consumes = "application/json")
    ResponseEntity<FeignResponseDTO> forward (@PathVariable("path") String path,
                                              @RequestBody String body);
}
