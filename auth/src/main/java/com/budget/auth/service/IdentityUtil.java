package com.budget.auth.service;

import com.budget.auth.util.CookieUtil;
import com.budget.auth.util.JwtUtil;
import com.budget.common.dto.AuthenticatedDTO;
import com.budget.common.dto.FeignLoginDTO;
import com.budget.common.dto.LoginDto;
import com.budget.common.exceptions.CriticalIncidentException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.security.MessageDigest;
import java.util.List;

@Component
public class IdentityUtil {
    private final BCryptPasswordEncoder encoder;
    private final MessageDigest sha256;
    private final ObjectMapper mapper;
    private final JwtUtil jwt;
    private final PrivateKey privateKey;
    private final CookieUtil cookieUtil;

    @Value("${security.fingerprint}")
    private String hashKey;

    public IdentityUtil(BCryptPasswordEncoder encoder,
                        MessageDigest sha256,
                        ObjectMapper mapper,
                        JwtUtil jwt,
                        PrivateKey privateKey,
                        CookieUtil cookieUtil){
        this.encoder = encoder;
        this.sha256 = sha256;
        this.mapper = mapper;
        this.jwt = jwt;
        this.privateKey = privateKey;
        this.cookieUtil = cookieUtil;
    }

    protected String stringEncoder (String raw){
        return encoder.encode(raw);
    }

    public String passwordFP (String rawPassword){
        byte[] hash = sha256.digest((rawPassword + hashKey).getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    protected String stringify (Object obj){
        try {
            return mapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    protected boolean authHandler (FeignLoginDTO dto, LoginDto credentials){
        if (encoder.matches(credentials.getPassword(), dto.getMsg())) {
            credentials.setAccess(jwt.generateAccessToken(credentials.getUsername(), dto.isAdmin() ? "admin" : "user"));
            credentials.setRefresh(jwt.generateRefreshToken(credentials.getUsername(), dto.isAdmin() ? "admin" : "user"));
            return true;
        }else {
            return false;
        }
    }

    protected String generateNewAccessToken(AuthenticatedDTO user){
        return jwt.generateAccessToken(
                user.getUsername(),
                user.getRole()
        );
    }

    protected List<String> logout () {
        List<String> tokens = new ArrayList<>();
        tokens.add(cookieUtil.removeAccessCookie(jwt.generateAccessToken("LOGOUT_USER", "NONE")));
        tokens.add(cookieUtil.removeRefreshCookie(jwt.generateRefreshToken("LOGOUT", "NONE")));
        return tokens;
    }

    public String internalSignatureHandler (String serviceName) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            String payload = String.format("serviceName=%s;timestamp=%s", serviceName, System.currentTimeMillis());
            signature.initSign(privateKey);
            signature.update(payload.getBytes());
            String sig = Base64.getEncoder().encodeToString(signature.sign());
            return payload + "|" + sig;
        } catch (Exception e) {
            throw new CriticalIncidentException (e.getMessage(), e);
        }
    }
}
