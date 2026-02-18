package com.budget.common.utilities;

import com.budget.common.client.GetWellKnown;
import com.budget.common.dto.AuthenticatedDTO;
import com.budget.common.dto.SignatureVerificationDTO;
import com.budget.common.exceptions.CriticalIncidentException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Component
@ConditionalOnProperty(
        name = "internal.auth",
        havingValue = "true",
        matchIfMissing = true
)
public class SignatureHandlerUtil {
    private final GetWellKnown getKey;

    private final AtomicReference<PublicKey> cachedKey = new AtomicReference<>();

    public SignatureHandlerUtil (GetWellKnown getKey){ this.getKey = getKey; }

    public String signatureVerification (String header, boolean attempt) {
        SignatureVerificationDTO dto = parseHeader(header);
        PublicKey key = cachedKey.get();
        try {
            Signature sig = Signature.getInstance(("SHA256withRSA"));
            sig.initVerify(key);
            sig.update(dto.getPayload());
            if (!sig.verify(dto.getSignature())){
                if (attempt)
                    throw new SecurityException("Invalid signature|" + header);
                getPublicKey();
                signatureVerification(header, true);
            }
            return dto.getUsername();
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e){
            throw new CriticalIncidentException(e.getMessage(), e);
        }
    }

    public AuthenticatedDTO extractDetails (String jwt){
        return new AuthenticatedDTO(
                extractClaims(jwt, Claims :: getSubject),
                extractClaims(jwt, claims -> claims.get("role", String.class))
        );
    }

    private <T> T extractClaims (String jwt, Function <Claims, T> claimResolver){
        return claimResolver.apply(extractAllClaims(jwt, false));
    }

    private Claims extractAllClaims (String jwt, boolean attempt){
        try{
            return Jwts.parser()
                    .verifyWith(cachedKey.get())
                    .requireIssuer("budget")
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        }catch (ExpiredJwtException e){
            //refresh logic
            return null; //TODO: remove after refresh logic implementation
        }catch (io.jsonwebtoken.security.SignatureException e){
            if (!attempt){
                getPublicKey();
                return extractAllClaims(jwt, true);
            }
            throw new SecurityException("Invalid JWT was attempted:"+jwt);
        }
    }

    public SignatureVerificationDTO parseHeader (String header){
        String[] headerParts = header.split("\\|");
        if (headerParts.length != 2)
            throw new SecurityException("Malformatted Authorization Header|" + header);
        String payload = headerParts[0];
        String signature = headerParts[1];
        String username = payload.split(";")[0].replace("username=", "");
        return new SignatureVerificationDTO(
                username,
                payload.getBytes(StandardCharsets.UTF_8),
                Base64.getDecoder().decode(signature));
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
