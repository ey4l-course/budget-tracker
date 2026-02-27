package com.budget.common.utilities;

import com.budget.common.client.GetWellKnown;
import com.budget.common.dto.AuthenticatedDTO;
import com.budget.common.dto.SignatureVerificationDTO;
import com.budget.common.exceptions.CriticalIncidentException;
import com.budget.common.exceptions.CustomSecurityException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

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
            if (System.currentTimeMillis() - dto.getTimeStamp() > 60000L)
                throw new AccessDeniedException("Internal token expired");
            Signature sig = Signature.getInstance(("SHA256withRSA"));
            sig.initVerify(key);
            sig.update(dto.getPayload());
            if (!sig.verify(dto.getSignature())){
                if (attempt)
                    throw new CustomSecurityException("invalid signature", "internal", header);
                getPublicKey();
                signatureVerification(header, true);
            }
            return dto.getService();
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e){
            throw new CriticalIncidentException(e.getMessage(), e);
        }
    }

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

    public SignatureVerificationDTO parseHeader (String header){
        String[] headerParts = header.split("\\|");
        if (headerParts.length != 2)
            throw new CustomSecurityException("Malformed Signature", "Internal", header);
        String payload = headerParts[0];
        String signature = headerParts[1];
        String service = payload.split(";")[0].replace("serviceName=", "");
        Long timestamp = Long.parseLong(payload.split(";")[1].replace("timestamp=", ""));
        return new SignatureVerificationDTO(
                service,
                timestamp,
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
