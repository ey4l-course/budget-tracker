package com.budget.gateway.dev;

import com.budget.common.dev.DevQueryClient;
import com.budget.common.dev.ServerProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/dev")
@Profile("dev") // Prevents accidental production deployment
public class DevController {
    private final Map<String, String> routes;
    private final DevQueryClient client; // A FeignClient targeting internal services

    public DevController(ServerProperties routes, DevQueryClient client) {
        this.routes = routes.getRoutes();
        this.client = client;
    }

    @PostMapping("/query/{service}")
    public ResponseEntity<?> executeDevQuery(@PathVariable ("service") String service, @RequestBody String query) {
        String url = routes.get(service);
        if (url == null)
            return ResponseEntity.badRequest().body("service not found");
        URI route = URI.create(url);
        return ResponseEntity.ok().body(client.forwardQuery(route, query));
    }
}