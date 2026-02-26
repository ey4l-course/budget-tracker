package com.budget.common.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
@ConditionalOnProperty(
        name = "internal.token",
        havingValue = "true",
        matchIfMissing = true
)
public class InternalTokenManager {
    private final AtomicReference<String> token = new AtomicReference<>();

    @Value("${security.cluster-key}")
    private String SECRET;

    @Value("${server.name}")
    private String SERVICE;

    public String getToken (){
        return token.get();
    }

    public void setToken (String newToken){
        this.token.set(newToken);
    }
}
