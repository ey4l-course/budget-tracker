package com.budget.common.dev;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@ConfigurationProperties
@Data
@Profile("dev")
public class ServerProperties {
    private Map<String, String> routes;
}
