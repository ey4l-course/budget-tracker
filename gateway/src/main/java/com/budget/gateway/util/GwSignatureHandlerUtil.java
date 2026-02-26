package com.budget.gateway.util;

import com.budget.common.client.GetWellKnown;
import com.budget.common.dto.AuthenticatedDTO;
import com.budget.common.dto.SignatureVerificationDTO;
import com.budget.common.exceptions.CriticalIncidentException;
import com.budget.common.exceptions.CustomSecurityException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class GwSignatureHandlerUtil {
    private final GetWellKnown getKey;

    private final AtomicReference<PublicKey> cachedKey = new AtomicReference<>();

    public GwSignatureHandlerUtil(GetWellKnown getKey){ this.getKey = getKey; }

    public AuthenticatedDTO extractDetails (String jwt){
        Claims c = extractAllClaims(jwt, false);
        return new AuthenticatedDTO(c.getSubject(), c.get("role", String.class));
    }

    private Claims extractAllClaims (String jwt, boolean attempt){
        try{
            return Jwts.parser()
                    .verifyWith(cachedKey.get())
                    .requireIssuer("budget")
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        }catch (io.jsonwebtoken.security.SignatureException e){
            if (!attempt){
                getPublicKey();
                return extractAllClaims(jwt, true);
            }
            throw new CustomSecurityException("Invalid token", "external", jwt);
        }
    }

    @PostConstruct
    private void getPublicKey () {
        String publicKeyStr = getKey.getAsset("public-key").getBody();
        if (publicKeyStr == null)
            throw new RuntimeException("Unable to obtain public key from auth");
        try {
            byte[] encoded = Base64.getDecoder().decode(publicKeyStr);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
            cachedKey.set(kf.generatePublic(spec));
        }catch (Exception e){
            throw new RuntimeException("Failed to boot. Public key or signature alg loading failed");
        }
    }
}
