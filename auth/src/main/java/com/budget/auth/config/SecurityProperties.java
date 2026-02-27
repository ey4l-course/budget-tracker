package com.budget.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

//@Configuration
@ConfigurationProperties (prefix = "security")
@Data
public class SecurityProperties {
    private String fingerprint;
    private String privateKeyPath;
    private String publicKeyPath;
    private Map<String, String> allowedSubnets;
    private Map<String, String> clusterKeys;
}
