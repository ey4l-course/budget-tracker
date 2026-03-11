package com.budget.common.dev;

import com.budget.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.util.List;
import java.util.Map;

@FeignClient (name = "DevQueryClient", configuration = FeignConfig.class)
public interface DevQueryClient {
    @PostMapping("/dev/direct-sql")
    List<Map<String, Object>> forwardQuery (URI targetService, @RequestBody String query);
}
