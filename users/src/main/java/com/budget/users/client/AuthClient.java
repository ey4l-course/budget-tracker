package com.budget.users.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "encryptString", url = "${routes.auth}")
public interface AuthClient {
    @RequestMapping(method = RequestMethod.GET, value = "/${path}")
    ResponseEntity<String> forward (@PathVariable ("path") String path,
                                    @RequestHeader (name = "X-raw-string") String rawString);
}
