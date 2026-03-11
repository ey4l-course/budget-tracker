package com.budget.common.dev;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dev")
@Profile("dev")
@ConditionalOnProperty(
        name = "internal.auth",
        havingValue = "true",
        matchIfMissing = true
)
public class DevReceiver {
    private final JdbcTemplate jdbc;

    public DevReceiver (JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @PostMapping("direct-sql")
    @PreAuthorize("hasAuthority('app')")
    public Object run (@RequestBody String sql){
        try {
            String trimmed = sql.trim().toUpperCase();
            if (trimmed.startsWith("SELECT")){
                return jdbc.queryForList(sql);
            }else {
                return List.of(Map.of("Affected rows", jdbc.update(sql)));
            }
        } catch (Exception e) {
            System.out.println("Exception cause: " + e.getCause());
            System.out.println("Exception message: " + e.getMessage());
            return List.of(Map.of("Error", e.getMessage()));
        }
    }
}
