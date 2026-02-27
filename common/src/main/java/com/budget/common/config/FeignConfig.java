package com.budget.common.config;

import com.budget.common.utilities.InternalTokenManager;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private final InternalTokenManager tokenManager;

    public FeignConfig(InternalTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = tokenManager.getToken();
            if (token != null) {
                requestTemplate.header("X-internal-Auth", token);
            }
        };
    }
}