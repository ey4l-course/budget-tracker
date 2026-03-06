package com.budget.common.utilities;

import com.budget.common.client.SecretService;
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
    private final SecretService secretService;

    public InternalTokenManager ( SecretService secretService) { this.secretService = secretService; }
    @Value("${security.cluster-key}")
    private String SECRET;

    @Value("${server.name}")
    private String SERVICE;

    public String current = token.get();

    public String getToken (){
        if (current == null || isTokenExpired())
            synchronized (this) {
                current = token.get();
                if (current == null || isTokenExpired()) {
                    newToken();
                    current = token.get();
                }
            }
        return current;
    }

    public void setToken (String newToken){
        this.token.set(newToken);
    }

    private void newToken () {
        this.setToken(secretService.getSigned(SECRET, SERVICE).getBody());
    }

    private boolean isTokenExpired () {
        try {
            String payload = current.split("\\|")[0];
            long timestamp = Long.parseLong(payload.split(";")[1].replace("timestamp=", ""));
            return System.currentTimeMillis() - timestamp > 600000;
        }catch (Exception e){
            return true;
        }

    }
}
